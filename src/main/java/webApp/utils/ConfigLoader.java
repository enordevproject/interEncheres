package webApp.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Map;
import java.io.IOException;

public class ConfigLoader {
    private static final String CONFIG_PATH = "/config/chatgpt.properties";
    private static Map<String, Object> gptProperties;

    @SuppressWarnings("unchecked")
    public static void loadProperties() {
        try (InputStream is = ConfigLoader.class.getResourceAsStream(CONFIG_PATH)) {
            if (is == null) {
                throw new RuntimeException("❌ Fichier de config introuvable dans resources : " + CONFIG_PATH);
            }
            ObjectMapper mapper = new ObjectMapper();
            gptProperties = mapper.readValue(is, Map.class);
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du chargement de " + CONFIG_PATH + " : " + e.getMessage());
            gptProperties = null;
        }
    }

    public static Map<String, Object> getGptProperties() {
        if (gptProperties == null) {
            loadProperties();
        }
        return gptProperties;
    }
}
