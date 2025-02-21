package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import base.BasePage;
import org.openqa.selenium.WebElement;

/*******************************************************************************************
 * Page Factory class for Interencheres Home Page
 * @author Your Name
 *******************************************************************************************/

public class Home extends BasePage {

    /**
     * Constructor to initialize the Home page.
     *
     * @param driver The WebDriver instance.
     */
    public Home(WebDriver driver) {
        super(driver);
    }

    /*******************************************************************************************
     * All WebElements are identified by locators
     *******************************************************************************************/

    // Locators
    private By loginButton = By.xpath("//button[contains(@class, 'v-btn') and .//div[text()='Se connecter']]");
    private By mailInput = By.xpath("//input[@autocomplete='username' and @type='email']");
    private By passInput = By.xpath("//input[@autocomplete='current-password' and @type='password']");
    private By submitLoginButton = By.xpath("//button[@type='submit']");
    private By closLoginCross = By.xpath("//button[@class='v-btn v-btn--icon v-btn--round theme--light v-size--default']//span[@class='v-btn__content']");
    private By searchInput = By.xpath("//*[@id=\"input-107\"]");
    private By searchButton = By.xpath("//button[@class='v-btn v-btn--has-bg theme--light elevation-0 v-size--small grey darken-1']");

    /*******************************************************************************************
     * All Methods for performing actions
     *******************************************************************************************/

    /**
     * Clicks the Login button on the home page.
     */
    public void clickLoginButton() {
        waitForElementToBeClickable(loginButton).click();
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
    public void enterEmail(String email) {
        WebElement emailField = waitForElementToBeClickable(mailInput);
        emailField.click(); // Click to focus on the email input field
        emailField.clear(); // Clear any existing text
        emailField.sendKeys(email); // Enter the email
        log.info("Entered email: " + email);
    }

    /**
     * Enters the password in the login form.
     *
     * @param password The password to enter.
     */
    public void enterPassword(String password) {
        WebElement passwordField = waitForElementToBeClickable(passInput);
        passwordField.click(); // Click to focus on the password input field
        passwordField.clear(); // Clear any existing text
        passwordField.sendKeys(password); // Enter the password
        log.info("Entered password.");
    }

    /**
     * Clicks the Submit button on the login form.
     */
    public void clickSubmitLoginButton() {
        waitForElementToBeClickable(submitLoginButton).click();
        log.info("Clicked on the Submit Login button.");
    }

    /**
     * Closes the login form by clicking the close button (cross).
     */
    public void closeLoginForm() {
        waitForElementToBeClickable(closLoginCross).click();
        log.info("Closed the login form.");
    }

    /**
     * Enters a search term in the search input field.
     *
     * @param searchTerm The term to search for.
     */
    public void enterSearchTerm(String searchTerm) {
        WebElement searchField = waitForElementToBeClickable(searchInput);
        searchField.click(); // Click to focus on the search input field
        searchField.clear(); // Clear any existing text
        searchField.sendKeys(searchTerm); // Enter the search term
        log.info("Entered search term: " + searchTerm);
    }

    /**
     * Clicks the Search button.
     */
    public void clickSearchButton() {
        waitForElementToBeClickable(searchButton).click();
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
        navigateToHomePage();
        try {
            // Click the Login button
            clickLoginButton();
            log.info("Clicked on the Login button.");

            // Enter the email
            enterEmail(email);
            log.info("Entered email: " + email);

            // Enter the password
            enterPassword(password);
            log.info("Entered password.");

            // Click the Submit Login button
            clickSubmitLoginButton();
            log.info("Clicked on the Submit Login button.");
        } catch (Exception e) {
            log.error("Failed to perform login: " + e.getMessage());
            throw e; // Re-throw the exception to fail the test
        }
    }
}