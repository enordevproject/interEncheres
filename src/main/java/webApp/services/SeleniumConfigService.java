package webApp.services;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import selenium.base.BasePage;

import java.io.File;
import java.time.Duration;

@Service
public class SeleniumConfigService extends BasePage {

    private static WebDriver driver; // ✅ Ensures only ONE instance

    public SeleniumConfigService() {
        super(); // ✅ Calls BasePage constructor
    }

    /**
     * ✅ Retrieves the WebDriver instance (Singleton pattern).
     * @return WebDriver instance
     */
    public synchronized WebDriver getDriver() {
        if (driver == null) { // ✅ Prevents multiple instances
            initDriver();
        }
        return driver;
    }

    /**
     * ✅ Initializes the WebDriver if not already initialized.
     */
    protected static void initDriver() {
        if (driver != null) return; // ✅ Prevent duplicate initialization

        String browser = config.getProperty("browser", "CHROME").toUpperCase(); // Default to Chrome

        switch (browser) {
            case "CHROME":
                if (driver == null) { // ✅ Double-check instance before creating
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("--no-sandbox", "--disable-gpu", "--disable-dev-shm-usage");
                    options.addArguments("--disable-blink-features=AutomationControlled"); // ✅ Bypass Cloudflare

                    if (config.getProperty("headless", "false").equalsIgnoreCase("true")) {
                        options.addArguments("--headless");
                        log.info("✅ Running Chrome in headless mode.");
                    }

                    driver = new ChromeDriver(options);
                    log.info("✅ ChromeDriver initialized successfully.");
                }
                break;

            default:
                throw new IllegalArgumentException("❌ Unsupported browser: " + browser);
        }

        // Set timeouts
        long implicitWait = Long.parseLong(config.getProperty("implicit.wait", "10"));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        log.info("⏳ Implicit wait set to " + implicitWait + " seconds.");

        // Set window maximization
        if (!browser.equals("HTMLUNIT")) {
            driver.manage().window().maximize();
        }
    }


    /**
     * ✅ Closes and properly quits the WebDriver instance.
     */
    public synchronized void closeDriver() {
        if (driver != null) {
            try {
                driver.quit(); // ✅ Ensure driver quits
                driver = null; // ✅ Prevent stale references
                log.info("✅ WebDriver closed successfully.");
            } catch (Exception e) {
                log.error("❌ Error closing WebDriver: " + e.getMessage());
            }
        }
    }
}
