package webApp.services;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class SeleniumConfigService {

    private static WebDriver driver; // ✅ Singleton WebDriver instance

    /**
     * ✅ Initializes WebDriver with different configurations for Local PC and VPS.
     */
    protected static void initDriver() {
        if (driver != null) return; // Prevent duplicate initialization

        ChromeOptions options = new ChromeOptions();

        // Detect if running on a VPS (Linux server) or Local Machine
        boolean isVPS = isRunningOnVPS();
        System.out.println("🖥️ Running on: " + (isVPS ? "VPS" : "Local PC"));

        // 🖥️ Common settings (for both Local and VPS)
        options.addArguments("--disable-blink-features=AutomationControlled"); // Anti-bot detection
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        // 📌 Set a real User-Agent to avoid detection
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36");

        // 📌 Prevent detection of Selenium automation
        options.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);

        // 📌 Inject secure headers (mimic real browsing behavior)
        Map<String, Object> headers = new HashMap<>();
        headers.put("Referer", "https://www.interencheres.com/");
        headers.put("Accept-Language", "en-US,en;q=0.9");
        options.setExperimentalOption("prefs", headers);

        // 🌍 VPS-Specific Configurations (Headless mode, Unique user-data-dir)
        if (isVPS) {
            options.addArguments("--headless=new"); // ✅ Keep headless but add more real-user settings
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36");
            options.addArguments("--user-data-dir=/tmp/selenium-profile-" + System.currentTimeMillis());
        }
        else {
            // 💻 Local PC Configurations (Normal mode with GUI)
            options.addArguments("--start-maximized"); // Open in maximized window
        }

        // ✅ Initialize WebDriver
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        System.out.println("✅ ChromeDriver initialized successfully on " + (isVPS ? "VPS" : "Local PC"));
    }

    /**
     * ✅ Detects if the code is running on a VPS (Linux server).
     */
    private static boolean isRunningOnVPS() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("linux") && !System.getenv().containsKey("DISPLAY"); // No GUI = VPS
    }

    /**
     * ✅ Retrieves the WebDriver instance (Singleton pattern).
     */
    public synchronized WebDriver getDriver() {
        if (driver == null) {
            initDriver();
        }
        return driver;
    }

    /**
     * ✅ Closes WebDriver properly.
     */
    public synchronized void closeDriver() {
        if (driver != null) {
            try {
                driver.quit();
                driver = null;
                System.out.println("✅ WebDriver closed successfully.");
            } catch (Exception e) {
                System.err.println("❌ Error closing WebDriver: " + e.getMessage());
            }
        }
    }
}
