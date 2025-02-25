package webApp.Utils;

import org.apache.commons.io.IOUtils;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class ImageUtils {

    /**
     * Downloads an image from the given URL and converts it to a Base64-encoded string.
     * @param imageUrl The URL of the image.
     * @return The Base64-encoded string of the image, or null if download fails.
     */
    public static String downloadImageAsBase64(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            System.out.println("⚠️ Aucune URL d'image fournie.");
            return null;
        }

        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                System.out.println("❌ Échec du téléchargement de l'image. Code réponse: " + responseCode);
                return null;
            }

            try (InputStream inputStream = connection.getInputStream()) {
                byte[] imageBytes = IOUtils.toByteArray(inputStream);
                return Base64.getEncoder().encodeToString(imageBytes);
            }
        } catch (Exception e) {
            System.out.println("❌ Erreur lors du téléchargement de l'image: " + e.getMessage());
            return null;
        }
    }
}
