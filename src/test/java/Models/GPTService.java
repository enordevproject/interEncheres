package Models;

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

            // 🔹 Définition des propriétés pour la fonction GPT
            Map<String, Object> properties = new HashMap<>();
            properties.put("lot_number", Map.of("type", "integer", "description", "Numéro de lot"));
            properties.put("description", Map.of("type", "string", "description", "Description complète"));
            properties.put("lot_url", Map.of("type", "string", "description", "URL de l'enchère"));
            properties.put("img_url", Map.of("type", "string", "nullable", true, "description", "URL de l'image"));
            properties.put("date", Map.of("type", "string", "description", "Date de l'enchère (YYYY-MM-DD)"));
            properties.put("maison_enchere", Map.of("type", "string", "description", "Maison d'enchères"));
            properties.put("quantity", Map.of("type", "integer", "description", "Quantité disponible"));
            properties.put("brand", Map.of("type", "string", "description", "Marque du laptop"));
            properties.put("model", Map.of("type", "string", "description", "Modèle du laptop"));
            properties.put("processor_brand", Map.of("type", "string", "description", "Marque du processeur"));
            properties.put("processor_model", Map.of("type", "string", "description", "Modèle du processeur"));
            properties.put("processor_cores", Map.of("type", "integer", "description", "Nombre de cœurs du processeur"));
            properties.put("processor_clock_speed", Map.of("type", "number", "description", "Fréquence du processeur en GHz"));
            properties.put("ram_size", Map.of("type", "integer", "description", "Taille de la RAM en GB"));
            properties.put("ram_type", Map.of("type", "string", "description", "Type de RAM (DDR4, DDR5, etc.)"));
            properties.put("storage_type", Map.of("type", "string", "description", "Type de stockage"));
            properties.put("storage_capacity", Map.of("type", "integer", "description", "Capacité du stockage en GB"));
            properties.put("gpu_type", Map.of("type", "string", "description", "Type de GPU (NVIDIA, AMD, etc.)"));
            properties.put("gpu_model", Map.of("type", "string", "description", "Modèle du GPU"));
            properties.put("gpu_vram", Map.of("type", "integer", "description", "Mémoire vidéo du GPU"));
            properties.put("screen_size", Map.of("type", "number", "description", "Taille de l'écran"));
            properties.put("screen_resolution", Map.of("type", "string", "description", "Résolution de l'écran"));
            properties.put("touch_screen", Map.of("type", "boolean", "description", "L'écran est-il tactile ?"));
            properties.put("fingerprint_sensor", Map.of("type", "boolean", "description", "Capteur d'empreintes digitales"));
            properties.put("face_recognition", Map.of("type", "boolean", "description", "Reconnaissance faciale"));
            properties.put("battery_life", Map.of("type", "string", "description", "Autonomie de la batterie"));
            properties.put("weight", Map.of("type", "number", "description", "Poids en kg"));
            properties.put("operating_system", Map.of("type", "string", "description", "Système d'exploitation"));
            properties.put("product_condition", Map.of("type", "string", "description", "État du laptop"));
            properties.put("warranty", Map.of("type", "string", "description", "Garantie"));
            properties.put("release_year", Map.of("type", "integer", "description", "Année de sortie"));
            properties.put("note_sur_10", Map.of("type", "integer", "description", "Note de qualité sur 10"));
            properties.put("reason_for_score", Map.of("type", "string", "description", "Raison de la note attribuée"));
            properties.put("recommended_to_buy", Map.of("type", "boolean", "description", "Recommandé à l'achat"));
            properties.put("etat_produit_image", Map.of("type", "string", "nullable", true, "description", "État du produit détecté par IA"));
            properties.put("reason_for_condition", Map.of("type", "string", "nullable", true, "description", "Raison des défauts détectés"));

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
