package selenium.pages;

import selenium.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/*******************************************************************************************
 * Page Factory class for Interencheres Home Page
 * @author Your Name
 *******************************************************************************************/

public class Home extends BasePage {

    // Locators
    private By loginButton = By.xpath("//button[contains(@class, 'v-btn') and .//div[text()='Se connecter']]");
    private By mailInput = By.xpath("//input[@autocomplete='username' and @type='email']");
    private By passInput = By.xpath("//input[@autocomplete='current-password' and @type='password']");
    private By submitLoginButton = By.xpath("//button[@type='submit']");
    private By closLoginCross = By.xpath("//button[@class='v-btn v-btn--icon v-btn--round theme--light v-size--default']//span[@class='v-btn__content']");
    private By searchInput = By.xpath("//input[@type='search' and @role='button']");
    private By searchButton = By.xpath("//button[@class='v-btn v-btn--has-bg theme--light elevation-0 v-size--small grey darken-1']");

    /**
     * Constructor to initialize the Home page.
     * @param driver The WebDriver instance.
     */
    public Home(WebDriver driver) {
        super(driver);
    }

    /*******************************************************************************************
     * All Methods for performing actions
     *******************************************************************************************/

    private void clickWhenReady(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    private void sendTextWhenReady(By locator, String text) {
        WebElement element = waitForElement(locator);
        element.clear();
        element.sendKeys(text);
    }

    /**
     * Clicks the Login button on the home page.
     */
    public void clickLoginButton() {
        clickWhenReady(loginButton);
        log.info("Clicked on the Login button.");
    }

    /**
     * Navigates to the home page of Interencheres.
     */
    public void navigateToHomePage() {
        driver.get("https://www.interencheres.com");
        log.info("Navigated to Interencheres home page.");
    }

    /**
     * Enters the email address in the login form.
     *
     * @param email The email address to enter.
     */
    /**
     * ✅ Enters the email address in the login form with improved waiting.
     * @param email The email address to enter.
     */
    public void enterEmail(String email) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // ✅ Wait for the input field to be present, visible, and enabled
            WebElement emailInputElement = wait.until(ExpectedConditions.elementToBeClickable(mailInput));

            // ✅ Ensure the input field is cleared before entering text
            emailInputElement.clear();
            Thread.sleep(500); // ✅ Small delay to ensure clearing works
            emailInputElement.sendKeys(email);

            log.info("✅ Entered email: " + email);
        } catch (Exception e) {
            log.error("❌ Failed to enter email: " + e.getMessage());
        }
    }


    /**
     * Enters the password in the login form.
     *
     * @param password The password to enter.
     */
    public void enterPassword(String password) {
        sendTextWhenReady(passInput, password);
        log.info("Entered password.");
    }

    /**
     * Clicks the Submit button on the login form.
     */
    public void clickSubmitLoginButton() {
        clickWhenReady(submitLoginButton);
        log.info("Clicked on the Submit Login button.");
    }

    /**
     * Closes the login form by clicking the close button (cross).
     */
    public void closeLoginForm() {
        clickWhenReady(closLoginCross);
        log.info("Closed the login form.");
    }

    /**
     * Enters a search term in the search input field.
     *
     * @param searchTerm The term to search for.
     */
    public void enterSearchTerm(String searchTerm) {
        try {
            Thread.sleep(2000); // Wait for 2 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            log.error("Thread sleep interrupted", e);
        }
        WebElement searchInputElement = waitForElement(searchInput);

        // Clear the input field by pressing Backspace the length of the current value
        String currentValue = searchInputElement.getAttribute("value");
        for (int i = 0; i < currentValue.length(); i++) {
            searchInputElement.sendKeys(Keys.BACK_SPACE); // Press Backspace for each character
        }

        // Wait for 2 seconds
        try {
            Thread.sleep(2000); // Wait for 2 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            log.error("Thread sleep interrupted", e);
        }

        // Enter the new search term
        searchInputElement.sendKeys(searchTerm);
        log.info("Entered search term: " + searchTerm);
    }


    /**
     * Clicks the Search button.
     */
    public void clickSearchButton() {
        clickWhenReady(searchButton);
        log.info("Clicked on the Search button.");
    }

    /**
     * Performs a search by entering a term and clicking the Search button.
     *
     * @param searchTerm The term to search for.
     */
    public void performSearch(String searchTerm) {

        enterSearchTerm(searchTerm);
        clickSearchButton();
        log.info("Performed search for: " + searchTerm);
    }

    public void performLogin(String email, String password) {
        try {
            navigateToHomePage();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            // ✅ Wait for login button to be clickable before clicking
            WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(loginButton));
            loginBtn.click();
            log.info("✅ Clicked Login Button.");

            // ✅ Enter email & password with extra waiting
            enterEmail(email);
            enterPassword(password);

            // ✅ Wait for Submit button and click
            WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(submitLoginButton));
            submitBtn.click();
            log.info("✅ Clicked Submit Login Button.");
        } catch (Exception e) {
            log.error("❌ Login failed: " + e.getMessage());
        }
    }


    public WebElement waitForElement(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Set timeout to 10 sec
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
}
