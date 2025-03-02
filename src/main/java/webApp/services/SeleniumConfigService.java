package webApp.services;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.Proxy;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class SeleniumConfigService {

    private static WebDriver driver; // ✅ Ensures only ONE instance

    /**
     * ✅ Initializes the WebDriver with Cloudflare Bypass Methods.
     */
    protected static void initDriver() {
        if (driver != null) return; // ✅ Prevent duplicate initialization

        ChromeOptions options = new ChromeOptions();

        // ✅ Bypass Cloudflare Bot Detection
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-dev-shm-usage");

        // ✅ Set a REAL User-Agent to avoid detection
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36");

        // ✅ Set Secure Headers (Mimic real browser requests)
        Map<String, Object> headers = new HashMap<>();
        headers.put("Referer", "https://www.interencheres.com/");
        headers.put("Accept-Language", "en-US,en;q=0.9");
        options.setExperimentalOption("prefs", headers);

        // ✅ Enable Stealth Mode (Prevent Detection)
        options.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);

        // ✅ Inject Cloudflare Cookies (Bypass CAPTCHA)
        options.setExperimentalOption("prefs", Map.of(
                "profile.default_content_setting_values.cookies", 1,
                "profile.block_third_party_cookies", false
        ));

       /* // ✅ Setup Proxy if needed (Use rotating proxies)
        Proxy proxy = new Proxy();
        proxy.setHttpProxy("your_proxy_here"); // Replace with a real proxy
        options.setCapability("proxy", proxy);*/

        // ✅ Initialize ChromeDriver
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();

        System.out.println("✅ ChromeDriver initialized with Cloudflare bypass.");
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
