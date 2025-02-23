package base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
            initDriver();
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

    private static void initConfig() {
        if (config == null) {
            try {
                config = new Properties();
                String config_fileName = "config.properties";
                String config_path = System.getProperty("user.dir") + File.separator + "config" + File.separator + config_fileName;
                FileInputStream config_ip = new FileInputStream(config_path);
                config.load(config_ip);
                log.info("Config file initialized.");

                data = new Properties();
                String data_fileName = "data.properties";
                String data_path = System.getProperty("user.dir") + File.separator + "config" + File.separator + data_fileName;
                FileInputStream data_ip = new FileInputStream(data_path);
                data.load(data_ip);
                log.info("Data file initialized.");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void initDriver() {
        if (driver != null) return;

        String browser = config.getProperty("browser");
        if (browser.equalsIgnoreCase("GoogleChrome") || browser.equalsIgnoreCase("CHROME")) {
            System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + File.separator + "drivers" + File.separator + "chromedriver.exe");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless", "--no-sandbox", "--blink-settings=imagesEnabled=false", "--log-level=3");
            driver = new ChromeDriver(options);
            log.info("Chrome driver initialized in headless mode with optimizations.");
        } else if (browser.equalsIgnoreCase("htmlunit")) {
            driver = new HtmlUnitDriver(true);
            log.info("HtmlUnit driver initialized.");
        } else {
            throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        long timeoutInSeconds = Long.parseLong(config.getProperty("implicit.wait", "10"));
        driver.manage().timeouts().implicitlyWait(timeoutInSeconds, TimeUnit.SECONDS);
        log.info("Implicit wait set to " + timeoutInSeconds + " seconds.");

        wait = new WebDriverWait(driver, Long.parseLong(config.getProperty("explicit.wait", "60")));
        log.info("Explicit wait set to 60 seconds.");

        if (!browser.equalsIgnoreCase("htmlunit")) {
            driver.manage().window().setSize(new Dimension(1920, 1080));
        }
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
            log.info("Closing Browser.");
        }
    }

    public WebElement waitForElement(By by) {
        return waitForElement(by, DEFAULT_TIMEOUT);
    }

    public WebElement waitForElement(By by, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
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

    // Additional Utility Functions
    protected void moveSlider(WebElement slider, double number, double max, double min) {
        int pixelsToMove = getPixelsToMove(slider, number, max, min);
        Actions sliderAction = new Actions(driver);
        sliderAction.clickAndHold(slider)
                .moveByOffset((-(int) slider.getSize().getWidth() / 2), 0)
                .moveByOffset(pixelsToMove, 0).release().perform();
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
