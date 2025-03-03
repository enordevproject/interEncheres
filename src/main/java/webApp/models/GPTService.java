package webApp.models;

import webApp.utils.ConfigLoader;
import webApp.utils.ImageUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class GPTService {

    public static Laptop generateLaptopFromLot(Lot lot) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Laptop generatedLaptop = null;

        // Load API configuration
        ApiInfo apiInfo = ApiInfoDAO.getApiInfoByName("Interencheres - Laptop");
        if (apiInfo == null) {
            System.out.println("❌ No API configuration found for OpenAI!");
            return null;
        }

        // Load GPT schema from ConfigLoader
        Map<String, Object> gptSchema = ConfigLoader.getGptProperties();
        if (gptSchema == null || gptSchema.isEmpty()) {
            System.out.println("❌ Error: GPT schema properties not found.");
            return null;
        }

        // Convert image to Base64 (if valid)
        String base64Image = ImageUtils.downloadImageAsBase64(lot.getImgUrl());
        boolean hasImage = (base64Image != null && !base64Image.isEmpty());

        // Prepare HTTP request
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(apiInfo.getApiUrl());
            request.setHeader("Authorization", "Bearer " + apiInfo.getApiKey());
            request.setHeader("Content-Type", "application/json");

            // Construct GPT request
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", apiInfo.getModel());
            requestBody.put("response_format", Map.of("type", "json_object"));
            requestBody.put("temperature", apiInfo.getTemperature());
            requestBody.put("max_tokens", apiInfo.getMaxTokens());
            requestBody.put("top_p", apiInfo.getTopP());
            requestBody.put("frequency_penalty", apiInfo.getFrequencyPenalty());
            requestBody.put("presence_penalty", apiInfo.getPresencePenalty());

            // System message
            List<Map<String, Object>> messages = new ArrayList<>();
            messages.add(Map.of(
                    "role", "system",
                    "content", "Tu es un expert en enchères de laptops. Rends exclusivement un JSON structuré, " +
                            "complet, conforme au schéma fourni. Analyse l'image en Base64 pour détecter " +
                            "les défauts (rayures, touches manquantes, fond blanc). Ne renvoie que le JSON."
            ));

            // User message (Lot details)
            messages.add(Map.of(
                    "role", "user",
                    "content", String.format(
                            """
                            Lot de laptop :
                            - Numéro : %s
                            - URL : %s
                            - Description : %s
                            - Maison d'enchères : %s
                            Rends seulement un JSON complet.
                            """,
                            lot.getNumber(), lot.getUrl(), lot.getDescription(), lot.getMaisonEnchere()
                    )
            ));

            // Add Image (if available)
            if (hasImage) {
                messages.add(Map.of(
                        "role", "user",
                        "content", "Voici l'image en Base64 pour inspection.",
                        "image", base64Image
                ));
            }

            requestBody.put("messages", messages);

            // Use GPT schema dynamically
            requestBody.put("tools", List.of(Map.of(
                    "type", "function",
                    "function", Map.of(
                            "name", "generate_laptop",
                            "description", "Analyse du lot et génération d'un JSON structuré pour un Laptop.",
                            "parameters", gptSchema
                    )
            )));
            requestBody.put("tool_choice", "required");

            // Log the full request being sent to GPT
            String jsonRequest = objectMapper.writeValueAsString(requestBody);


            // Send request
            StringEntity entity = new StringEntity(jsonRequest, ContentType.APPLICATION_JSON);
            request.setEntity(entity);
            System.out.println("📤 Sending request to GPT...");

            // Read response
            try (CloseableHttpResponse response = httpClient.execute(request);
                 InputStream responseStream = response.getEntity().getContent()) {

                String rawJson = new BufferedReader(new InputStreamReader(responseStream))
                        .lines()
                        .collect(Collectors.joining("\n"));

                System.out.println("📥 JSON Response:\n" + rawJson);

                // Parse JSON response
                JsonNode root = objectMapper.readTree(rawJson);

                // Check for errors
                if (!root.path("error").isMissingNode()) {
                    String err = root.path("error").path("message").asText("");
                    System.out.println("❌ GPT error: " + err);
                    return null;
                }

                // Extract choices
                JsonNode choices = root.path("choices");
                if (!choices.isArray() || choices.isEmpty()) {
                    System.out.println("❌ 'choices' missing or empty.");
                    return null;
                }

                // Extract tool_calls
                JsonNode toolCalls = choices.get(0).path("message").path("tool_calls");
                if (!toolCalls.isArray() || toolCalls.isEmpty()) {
                    System.out.println("❌ 'tool_calls' missing or empty.");
                    return null;
                }

                // Extract arguments
                JsonNode argsNode = toolCalls.get(0).path("function").path("arguments");
                if (argsNode.isMissingNode()) {
                    System.out.println("❌ 'arguments' missing.");
                    return null;
                }

                JsonNode parsedArgs = objectMapper.readTree(argsNode.asText());
                generatedLaptop = objectMapper.treeToValue(parsedArgs, Laptop.class);
                if (generatedLaptop == null) {
                    System.out.println("❌ Error: Laptop not generated.");
                    return null;
                }

                System.out.println("✅ Laptop generated: " + generatedLaptop.getModel());
                generatedLaptop.setImgUrl(lot.getImgUrl());
                generatedLaptop.setDate(lot.getDate());
                generatedLaptop.setMaisonEnchere(lot.getMaisonEnchere()); // ✅ Fix: Set correct auction house


                // Insert into database
                Results.insertLaptopIntoDatabase(generatedLaptop);
            }
        } catch (Exception e) {
            System.out.println("❌ Unexpected error: " + e.getMessage());
        }
        return generatedLaptop;
    }
}
