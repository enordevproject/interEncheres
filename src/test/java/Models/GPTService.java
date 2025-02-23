package Models;

import Utils.ConfigLoader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hibernate.HibernateUtil;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.hibernate.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class GPTService {
    public static Laptop generateLaptopFromLot(Lot lot) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Laptop generatedLaptop = null;

        // Charger la configuration API depuis la base de données
        ApiInfo apiInfo = ApiInfoDAO.getApiInfoByName("Interencheres - Laptop");

        if (apiInfo == null) {
            System.out.println("❌ Aucune configuration API trouvée pour OpenAI !");
            return null;
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(apiInfo.getApiUrl());
            request.setHeader("Authorization", "Bearer " + apiInfo.getApiKey());
            request.setHeader("Content-Type", "application/json");

            // ✅ Construction du JSON pour GPT-4 Turbo
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", apiInfo.getModel());
            requestBody.put("response_format", Map.of("type", "json_object"));
            requestBody.put("temperature", apiInfo.getTemperature());
            requestBody.put("max_tokens", apiInfo.getMaxTokens());
            requestBody.put("top_p", apiInfo.getTopP());
            requestBody.put("frequency_penalty", apiInfo.getFrequencyPenalty());
            requestBody.put("presence_penalty", apiInfo.getPresencePenalty());

            Map<String, Object> properties = ConfigLoader.getGptProperties();

            // 🔹 Messages pour GPT
            List<Map<String, Object>> messages = List.of(
                    Map.of("role", "system", "content", "Tu es un expert en enchères de laptops. Fournis un JSON structuré."),
                    Map.of("role", "user", "content", String.format(
                            "Voici un lot de laptop provenant d’une enchère :\n"
                                    + "- Numéro du lot : %s\n"
                                    + "- URL du lot : %s\n"
                                    + "- Description du lot : %s\n"
                                    + "- Maison d'enchères : %s\n"
                                    + "- Image : %s\n"
                                    + "Retourne uniquement du JSON structuré et complet.",
                            lot.getNumber(), lot.getUrl(), lot.getDescription(), lot.getMaisonEnchere(),
                            lot.getImgUrl() != null ? lot.getImgUrl() : "Aucune image disponible"
                    ))
            );

            requestBody.put("messages", messages);

            requestBody.put("tools", List.of(Map.of(
                    "type", "function",
                    "function", Map.of(
                            "name", "generate_laptop",
                            "description", "Génère un objet Laptop à partir des données d'enchères.",
                            "parameters", Map.of(
                                    "type", "object",
                                    "properties", properties,
                                    "required", properties.keySet()
                            )
                    )
            )));

            StringEntity entity = new StringEntity(objectMapper.writeValueAsString(requestBody), ContentType.APPLICATION_JSON);
            request.setEntity(entity);

            System.out.println("📤 Envoi de la requête à GPT-4...");
            try (CloseableHttpResponse response = httpClient.execute(request);
                 InputStream responseStream = response.getEntity().getContent()) {

                // Convert response to a string for debugging
                String jsonResponse = new BufferedReader(new InputStreamReader(responseStream))
                        .lines().collect(Collectors.joining("\n"));

                // Print full JSON response for debugging
                System.out.println("📥 JSON Response from GPT-4:\n" + jsonResponse);

                // Parse the response into a JSON object
                JsonNode rootNode = objectMapper.readTree(jsonResponse);

                // Validate that the response has expected fields
                if (!rootNode.has("choices") || rootNode.get("choices").isEmpty()) {
                    System.out.println("❌ Erreur : 'choices' manquant dans la réponse JSON.");
                    return null;
                }

                // Extract the tool call arguments
                JsonNode toolCallNode = rootNode.path("choices").get(0)
                        .path("message").path("tool_calls")
                        .get(0).path("function").path("arguments");

                // Debugging: Print extracted JSON before parsing
                System.out.println("📌 Extracted JSON Arguments:\n" + toolCallNode.toPrettyString());

                // Convert toolCallNode from a **stringified JSON** into a proper JSON object
                JsonNode parsedArguments = objectMapper.readTree(toolCallNode.asText());

                // Deserialize properly
                generatedLaptop = objectMapper.treeToValue(parsedArguments, Laptop.class);


                // Debugging: Print generated laptop attributes
                if (generatedLaptop != null) {
                    System.out.println("✅ Laptop généré avec succès : ");
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
            return generatedLaptop;

        }
    }
}
