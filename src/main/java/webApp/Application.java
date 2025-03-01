package webApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

@SpringBootApplication
public class Application {

    // Spring Boot va servir les fichiers du dossier src/main/resources/static
    // Sur le port défini dans application.properties (ex: 9090)
    // Pour un fichier laptop_inventory.html dans /static, l’URL sera :
    // http://localhost:9090/laptop_inventory.html

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("🚀 Spring Boot Application Started Successfully! ✅");

        // On ouvre l’URL sur le port Spring (ex: 9090)
        // On suppose que laptop_inventory.html se trouve dans src/main/resources/static
        String localUrl = "http://localhost:9090/laptop_inventory.html";
        openChrome(localUrl);
    }

    private static void openChrome(String url) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                // Windows
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "chrome", url});
            } else if (os.contains("mac")) {
                // macOS
                Runtime.getRuntime().exec(new String[]{"open", "-a", "Google Chrome", url});
            } else {
                // Linux
                Runtime.getRuntime().exec(new String[]{"google-chrome", url});
            }
            System.out.println("🌍 Attempting to open Chrome with URL: " + url);
        } catch (IOException e) {
            System.err.println("❌ Failed to open Chrome: " + e.getMessage());
            fallbackToDefaultBrowser(url);
        } catch (Exception e) {
            System.err.println("❌ Unexpected error opening Chrome: " + e.getMessage());
            fallbackToDefaultBrowser(url);
        }
    }

    private static void fallbackToDefaultBrowser(String url) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(url));
                System.out.println("🌍 Opened in default browser: " + url);
            } catch (Exception ex) {
                System.err.println("❌ Even the default browser failed: " + ex.getMessage());
            }
        } else {
            System.out.println("⚠️ Desktop browsing not supported on this system.");
        }
    }
}
