package webApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.ApplicationContext;

import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Application.class, args);
        System.out.println("🚀 Spring Boot Application Started Successfully! ✅");

        // Retrieve dynamic base URL
        String baseUrl = getBaseUrl(context);
        String appUrl = baseUrl + "/laptop_inventory.html";

        openChrome(appUrl);
    }

    private static String getBaseUrl(ApplicationContext context) {
        try {
            WebServerApplicationContext serverContext = (WebServerApplicationContext) context;
            int port = serverContext.getWebServer().getPort();
            String host = InetAddress.getLocalHost().getHostAddress(); // Get local IP instead of hardcoded localhost

            return "http://" + host + ":" + port; // Dynamic URL
        } catch (UnknownHostException e) {
            System.err.println("❌ Error retrieving host address: " + e.getMessage());
            return "http://localhost:9090"; // Fallback
        }
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
