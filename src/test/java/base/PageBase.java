package base;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PageBase {

    /**
     * logger for class.
     */
    private static final Logger logger = Logger.getLogger(PageBase.class);

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
    protected static WebDriver driver;

    /**
     * default constructor.
     */
    public PageBase(WebDriver driver) {
        setDriver(driver);
    }

    public WebDriver getDriver() {
        return driver;
    }

    public static void setDriver(WebDriver driver) {
        PageBase.driver = driver;
    }

    /**
     * Waiting WebElement By to disappear, 20 seconds by default
     *
     * @param webElementBy
     */
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
    public WebElement waitForElementToBeClickable(By webElementBy) {
        WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT); // Use long for timeout
        wait.until(ExpectedConditions.jsReturnsValue("return jQuery.active == 0"));
        return wait.until(ExpectedConditions.elementToBeClickable(findElement(webElementBy)));
    }

    public WebElement waitForElementToBeVisible(By webElementBy) {
        WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT); // Use long for timeout
        return wait.until(ExpectedConditions.visibilityOfElementLocated(webElementBy));
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







    protected boolean checkWebElementVisible(By webElement) {
        WebElement findElement = findElement(webElement);
        if (findElement == null)
            return false;
        return findElement.isDisplayed();
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
