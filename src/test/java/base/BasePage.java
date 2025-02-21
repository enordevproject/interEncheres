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
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {

    /**
     * logger for class.
     */
    protected static final Logger log = Logger.getLogger(BasePage.class);

    /**
     * by default 20s to wait
     */
    private final int DEFAULT_TIMEOUT = 25;

    /**
     * explicit wait
     */
    private final int TIMEOUT = 50;

    /**
     * Current driver
     */
    private static boolean isInitalized=false;
    protected static WebDriver driver;

    /**
     * WebDriverWait for explicit waits
     */
    protected static WebDriverWait wait;

    /**
     * Configuration properties
     */
    protected static Properties config = null;

    /**
     * Data properties
     */
    protected static Properties data = null;

    /**
     * Initialize logs, config, and driver
     */
    protected  BasePage() {
        if(!isInitalized){
            initLogs();
            initConfig();
            initDriver();
        }
    }

    public BasePage(WebDriver driver) {
        this.driver = driver;
    }
    /**
     * Initialize Logger.
     */
    private static void initLogs() {
        if (log == null) {
            DOMConfigurator.configure(System.getProperty("user.dir") + File.separator + "config" + File.separator + "log4j.xml");
            log.info("Logger is initialized..");
        }
    }

    /**
     * Initialize configuration and data properties.
     */
    private static void initConfig() {
        if (config == null) {
            try {
                // Initialize config properties file
                config = new Properties();
                String config_fileName = "config.properties";
                String config_path = System.getProperty("user.dir") + File.separator + "config" + File.separator + config_fileName;
                FileInputStream config_ip = new FileInputStream(config_path);
                config.load(config_ip);
                log.info("Config file initialized.");

                // Initialize data properties file
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

    /**
     * Initialize WebDriver based on configuration.
     */
    private static void initDriver() {
        if (driver != null) {
            return; // Driver is already initialized
        }

        String browser = config.getProperty("browser");

        if (browser.equalsIgnoreCase("GoogleChrome") || browser.equalsIgnoreCase("CHROME")) {
            // Set the path to ChromeDriver
            System.setProperty("webdriver.chrome.driver",
                    System.getProperty("user.dir") + File.separator + "drivers" + File.separator + "chromedriver.exe");

            // Configure ChromeOptions
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--no-sandbox"); // Disable sandbox for headless mode
            options.addArguments("--disable-dev-shm-usage"); // Overcome limited resource problems
            options.addArguments("--headless"); // Run in headless mode
            options.addArguments("--window-size=1280,1024"); // Set window size for headless mode

            // Initialize ChromeDriver
            driver = new ChromeDriver(options);
            log.info("Chrome driver is initialized in headless mode.");
        } else if (browser.equalsIgnoreCase("htmlunit")) {
            // Initialize HtmlUnitDriver
            driver = new HtmlUnitDriver();
            log.info("HtmlUnit driver is initialized.");
        } else {
            throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        // Configure implicit wait (fetch timeout from config.properties)
        String waitTime = config.getProperty("implicit.wait", "30"); // Default to 30 seconds if not specified
        driver.manage().timeouts().implicitlyWait(Long.parseLong(waitTime), TimeUnit.SECONDS);
        log.info("Implicit wait set to " + waitTime + " seconds.");

        // Maximize the browser window (if not in headless mode)
        if (!browser.equalsIgnoreCase("htmlunit")) {
            driver.manage().window().maximize();
            log.info("Browser window maximized.");
        }

        // Initialize WebDriverWait (fetch timeout from config.properties)
        String explicitWaitTime = config.getProperty("explicit.wait", "120"); // Default to 120 seconds if not specified
        wait = new WebDriverWait(driver, Long.parseLong(explicitWaitTime));
        log.info("Explicit wait set to " + explicitWaitTime + " seconds.");
    }
    /**
     * Quit Driver.
     */
    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
            log.info("Closing Browser.");
        }
    }
    public void waitForElementToDisappear(By webElementBy) {
        waitForElementToDisappear(webElementBy, DEFAULT_TIMEOUT); // assuming DEFAULT_TIMEOUT is an integer value
    }

    public void waitForElementToDisappear(By webElementBy, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds); // timeoutInSeconds is passed as a long value
        wait.until(ExpectedConditions.invisibilityOfElementLocated(webElementBy));
    }


    /**
     * Method to wait element to be clickable
     *
     * @param webElementBy
     */
    public WebElement waitForElementToBeClickable(WebElement webElementBy) {
        WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
        wait.until(ExpectedConditions.jsReturnsValue("return jQuery.active == 0"));
        return wait.until(ExpectedConditions.elementToBeClickable(webElementBy));
    }


    public void mouseOver(By webelement) {
        WebElement element = driver.findElement(webelement);
        Actions builder = new Actions(driver);
        builder.moveToElement(element).perform();
    }

    /**
     * Method to wait element to be clickable
     *
     * @param webElementBy
     */

    public WebElement waitForElementToBeClickable(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement waitForElementToBeVisible(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT); // Use long for timeout
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForElementToBeVisibleExplicitly(By webElementBy) {
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT); // Use long for timeout
        return wait.until(ExpectedConditions.visibilityOfElementLocated(webElementBy));
    }


    /**
     * Waiting WebElement By to disappear
     *
     * @return
     */
    public WebElement waitForElement(By by) {
        return waitForElement(by, DEFAULT_TIMEOUT);
    }

    /**
     * Waiting WebElement By to disappear
     *
     * @param by
     */
    public WebElement waitForElement(By by, int waitSecond) {
        WebDriverWait wait = new WebDriverWait(driver, (long) waitSecond); // cast the int to long
        ExpectedCondition<Boolean> elementIsDisplayed = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver arg0) {
                try {
                    driver.findElement(by).isDisplayed();
                    return true;
                } catch (NoSuchElementException | StaleElementReferenceException e) {
                    return false;
                }
            }
        };
        wait.until(elementIsDisplayed);
        return driver.findElement(by);
    }









    protected void clickWithJavaScript(WebElement webElement) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].focus();", webElement);
        js.executeScript("arguments[0].click();", webElement);
    }

    protected void clickWithJavaScript(By webElement) {
        clickWithJavaScript(findElement(webElement));
    }









    /**
     * @param select, need to be a SELECT tag
     */
    protected void deselectAll(WebElement select) {
        new Select(select).deselectAll();
    }

    protected void deselectAll(By select) {
        deselectAll(findElement(select));
    }



    protected String findTextOfElement(WebElement webElement) {
        return webElement.getText();
    }

    protected String findTextOfElement(By webElement) {
        return driver.findElement(webElement).getText();
    }

    protected String findClassOfElement(By webElement) {
        return driver.findElement(webElement).getAttribute("class");
    }

    protected String findClassOfElement(WebElement webElement) {
        return webElement.getAttribute("class");
    }

    protected String findDateOfElement(By webElement) {
        return driver.findElement(webElement).getAttribute("data-date");
    }

    protected String findDateOfElement(WebElement webElement) {
        return webElement.getAttribute("data-date");
    }

    protected String findValueOfElement(By webElement) {
        return driver.findElement(webElement).getAttribute("Value");
    }

    protected WebElement findElement(By webElement) {
        return driver.findElement(webElement);
    }

    protected List<WebElement> findElements(By webElement) {
        return driver.findElements(webElement);
    }

    // Common Actions

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
        JavascriptExecutor js = (JavascriptExecutor) driver; // scrolls the page until the element is in full view
        js.executeScript("arguments[0].scrollIntoView();", webElement);
    }

    public String getInnerHtml(WebElement element) {
        String text = ((JavascriptExecutor) driver).executeScript("return arguments[0].innerHTML", element).toString();
        text = text.replaceAll("\n", "").replaceAll("\t", "").trim();
        return text;
    }


    /**
     * method to click on "ok" when alert javascript shows
     */
    public void acceptAlert() {
        driver.switchTo().alert().accept();
    }


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

    /**
     * get the departure date in format "EEEE dd MMMM yyyy" and return it in "dd/MM/yyyy"
     */
}
