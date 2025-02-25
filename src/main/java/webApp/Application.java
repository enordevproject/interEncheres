package webApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("🚀 Spring Boot Application Started Successfully! ✅");

        // Auto-open the browser to the laptop inventory page
        openBrowser("http://localhost:9090/api/laptops");
    }

    private static void openBrowser(String url) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(url));
                System.out.println("🌍 Browser opened: " + url);
            } catch (IOException | RuntimeException | java.net.URISyntaxException e) {
                System.err.println("❌ Failed to open browser: " + e.getMessage());
            }
        } else {
            System.out.println("⚠️ Desktop browsing not supported on this system.");
        }
    }
}
