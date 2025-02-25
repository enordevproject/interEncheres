package webApp.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ConfigLoader {
    private static final String CONFIG_FILE = "config/chatgpt.properties";
    private static Map<String, Object> gptProperties;

    /**
     * Loads GPT properties from a JSON file (`chatgpt.properties`).
     */
    public static void loadProperties() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File file = new File(CONFIG_FILE);
            if (!file.exists()) {
                throw new RuntimeException("❌ Le fichier de configuration est introuvable: " + CONFIG_FILE);
            }
            gptProperties = objectMapper.readValue(file, Map.class);
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du chargement des propriétés: " + e.getMessage());
            gptProperties = null;
        }
    }

    /**
     * Returns the GPT properties as a Map.
     */
    public static Map<String, Object> getGptProperties() {
        if (gptProperties == null) {
            loadProperties();
        }
        return gptProperties;
    }
}
