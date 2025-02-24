package Models;

import  Utils.ConfigLoader;
import Utils.ImageUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
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

        // ✅ Load API Configuration
        ApiInfo apiInfo = ApiInfoDAO.getApiInfoByName("Interencheres - Laptop");
        if (apiInfo == null) {
            System.out.println("❌ Aucune configuration API trouvée pour OpenAI !");
            return null;
        }


        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(apiInfo.getApiUrl());
            request.setHeader("Authorization", "Bearer " + apiInfo.getApiKey());
            request.setHeader("Content-Type", "application/json");

            // ✅ Load GPT properties from config
            Map<String, Object> properties = ConfigLoader.getGptProperties();
            if (properties == null || properties.isEmpty()) {
                System.out.println("❌ Erreur : Les propriétés GPT sont introuvables.");
                return null;
            }

            // ✅ Fetch and convert image to Base64
            String base64Image = ImageUtils.downloadImageAsBase64(lot.getImgUrl());
            boolean hasImage = base64Image != null && !base64Image.isEmpty();

            // ✅ Construct the JSON payload
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", apiInfo.getModel());
            requestBody.put("response_format", Map.of("type", "json_object"));
            requestBody.put("temperature", apiInfo.getTemperature());
            requestBody.put("max_tokens", apiInfo.getMaxTokens());
            requestBody.put("top_p", apiInfo.getTopP());
            requestBody.put("frequency_penalty", apiInfo.getFrequencyPenalty());
            requestBody.put("presence_penalty", apiInfo.getPresencePenalty());

            // ✅ Construct GPT messages
            List<Map<String, Object>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", "Expert en enchères de laptops, génère un JSON structuré avec des spécifications précises. Vérifie l'authenticité du lot et détecte les défauts visibles. Si l'image a un fond blanc, elle peut être générée et non réelle.\""));

            messages.add(Map.of("role", "user", "content", String.format(
                    "Voici un lot de laptop provenant d’une enchère :\n"
                            + "- Numéro du lot : %s\n"
                            + "- URL du lot : %s\n"
                            + "- Description du lot : %s\n"
                            + "- Maison d'enchères : %s\n"
                            + "Retourne uniquement du JSON structuré et complet.",
                    lot.getNumber(), lot.getUrl(), lot.getDescription(), lot.getMaisonEnchere()
            )));

            // ✅ Include image data in the request if available
            if (hasImage) {
                messages.add(Map.of(
                        "role", "user",
                        "content", "Voici une image du lot en Base64. Analyse attentivement : si une image est fournie, détecte les défauts visibles (rayures, fissures, touches manquantes, écran endommagé). Si le fond est blanc, l'image peut être générée et non réelle.",
                        "image", base64Image
                ));
            }

            requestBody.put("messages", messages);

            // ✅ Define the tool function to be called
            requestBody.put("tools", List.of(Map.of(
                    "type", "function",
                    "function", Map.of(
                            "name", "generate_laptop",
                            "description", " ",
                            "parameters", Map.of(
                                    "type", "object",
                                    "properties", properties,
                                    "required", properties.keySet()
                            )
                    )
            )));

            // ✅ Force GPT to call the function
            requestBody.put("tool_choice", "required");

            // ✅ Send API request
            StringEntity entity = new StringEntity(objectMapper.writeValueAsString(requestBody), ContentType.APPLICATION_JSON);
            request.setEntity(entity);

            System.out.println("📤 Envoi de la requête à GPT-4...");
            try (CloseableHttpResponse response = httpClient.execute(request);
                 InputStream responseStream = response.getEntity().getContent()) {

                // ✅ Convert response to string
                String jsonResponse = new BufferedReader(new InputStreamReader(responseStream))
                        .lines().collect(Collectors.joining("\n"));

                System.out.println("📥 JSON Response from GPT-4:\n" + jsonResponse);

                // ✅ Parse JSON response
                JsonNode rootNode = objectMapper.readTree(jsonResponse);

                // ✅ Validate response structure
                JsonNode choices = rootNode.path("choices");
                if (!choices.isArray() || choices.isEmpty()) {
                    System.out.println("❌ Erreur : 'choices' manquant ou vide dans la réponse JSON.");
                    return null;
                }

                JsonNode firstChoice = choices.get(0);
                JsonNode toolCalls = firstChoice.path("message").path("tool_calls");

                if (!toolCalls.isArray() || toolCalls.isEmpty()) {
                    System.out.println("❌ Erreur : 'tool_calls' absent ou vide dans la réponse JSON.");
                    return null;
                }

                // ✅ Extract function arguments
                JsonNode toolCallNode = toolCalls.get(0).path("function").path("arguments");

                if (toolCallNode.isMissingNode()) {
                    System.out.println("❌ Erreur : 'arguments' absent ou mal formé.");
                    return null;
                }

                // ✅ Convert function arguments from stringified JSON
                JsonNode parsedArguments = objectMapper.readTree(toolCallNode.asText());

                // ✅ Deserialize JSON to Laptop object
                generatedLaptop = objectMapper.treeToValue(parsedArguments, Laptop.class);

                // ✅ Save to database
                if (generatedLaptop != null) {
                    System.out.println("✅ Laptop généré avec succès : " + generatedLaptop);
                    generatedLaptop.setImgUrl(lot.getImgUrl());
                    generatedLaptop.setDate(lot.getDate());
                    Results.insertLaptopIntoDatabase(generatedLaptop);
                } else {
                    System.out.println("❌ Erreur : Laptop non généré correctement.");
                }

            } catch (JsonProcessingException e) {
                System.out.println("❌ Erreur JSON Processing : " + e.getMessage());
            } catch (IOException e) {
                System.out.println("❌ Erreur I/O lors de la requête API : " + e.getMessage());
            } catch (Exception e) {
                System.out.println("❌ Erreur inattendue : " + e.getMessage());
            }
        }
        return generatedLaptop;
    }
}






