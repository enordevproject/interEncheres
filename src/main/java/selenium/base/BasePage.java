package selenium.base;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public abstract class BasePage {

    protected static final Logger log = Logger.getLogger(BasePage.class);
    private static final int DEFAULT_TIMEOUT = 25;
    private static boolean isInitialized = false;
    protected static WebDriver driver;
    protected static WebDriverWait wait;
    protected static Properties config = null;
    protected static Properties data = null;

    protected BasePage() {
        if (!isInitialized) {
            initLogs();
            initConfig();
          //  initDriver();
        }
    }

    public BasePage(WebDriver driver) {
        this.driver = driver;
    }

    private static void initLogs() {
        if (log == null) {
            DOMConfigurator.configure(System.getProperty("user.dir") + File.separator + "config" + File.separator + "log4j.xml");
            log.info("Logger is initialized..");
        }
    }

    public void initConfig() {
        config = new Properties();
        data = new Properties();

        try (InputStream configStream = getClass().getClassLoader().getResourceAsStream("config/config.properties");
             InputStream dataStream = getClass().getClassLoader().getResourceAsStream("config/data.properties")) {

            if (configStream == null) {
                throw new IOException("❌ config.properties file not found in resources/config/");
            }
            if (dataStream == null) {
                throw new IOException("❌ data.properties file not found in resources/config/");
            }

            config.load(configStream);
            data.load(dataStream);
            log.info("✅ Config and data files initialized successfully.");

        } catch (IOException e) {
           // log.error("❌ Error loading properties files: {}", e.getMessage(), e);
        }
    }




    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
            log.info("Closing Browser.");
        }
    }




    protected void clickWithJavaScript(WebElement webElement) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].focus();", webElement);
        js.executeScript("arguments[0].click();", webElement);
    }

    protected void clickWithJavaScript(By webElement) {
        clickWithJavaScript(findElement(webElement));
    }

    protected WebElement findElement(By webElement) {
        return driver.findElement(webElement);
    }

    protected List<WebElement> findElements(By webElement) {
        return driver.findElements(webElement);
    }

    public void switchToNewWindow() {
        String currentWindow = driver.getWindowHandle();
        Set<String> allWindows = driver.getWindowHandles();
        for (String window : allWindows) {
            if (!window.equals(currentWindow)) {
                driver.switchTo().window(window);
                break;
            }
        }
    }

    protected void scrollToWebElement(By webElement) {
        scrollToWebElement(findElement(webElement));
    }

    protected void scrollToWebElement(WebElement webElement) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView();", webElement);
    }

    public void acceptAlert() {
        driver.switchTo().alert().accept();
    }



    protected static int getPixelsToMove(WebElement slider, double amount, double sliderMax, double sliderMin) {
        int pixels = 0;
        double tempPixels = slider.getSize().getWidth();
        tempPixels = tempPixels / (sliderMax - sliderMin);
        tempPixels = tempPixels * (amount - sliderMin);
        pixels = (int) tempPixels;
        return pixels;
    }

    // Optional Universal Logging for errors and retries
    public void logErrorAndRetry(Runnable action, int retries) {
        int attempt = 0;
        while (attempt < retries) {
            try {
                action.run();
                break;
            } catch (Exception e) {
                log.error("Attempt " + (attempt + 1) + " failed: " + e.getMessage());
                attempt++;
            }
        }
    }
}
