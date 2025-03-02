package selenium.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.base.BasePage;
import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;

public class Produit extends BasePage {

    private By favorisButton = By.xpath("//button[@class='v-btn v-btn--outlined v-btn--text theme--light v-size--default primary--text']");
    private By favorisIcon = By.xpath("//button[contains(@class, 'v-btn') and .//i[contains(@class, 'mdi-heart-outline')]]");


    /**
     * Constructor to initialize the Produit page.
     *
     * @param driver The WebDriver instance.
     */
    public Produit(WebDriver driver) {
        super(driver);
    }

    /**
     * Opens a lot page using its URL.
     *
     * @param lotUrl The URL of the lot.
     */
    public void openLotPage(String lotUrl) {
        driver.get(lotUrl);
        log.info("🌐 Navigated to lot page: " + lotUrl);
    }

    /**
     * Checks if the lot is already in favorites.
     *
     * @return true if the lot is already in favorites, false otherwise.
     */
    public boolean isLotInFavorites() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement favIcon = wait.until(ExpectedConditions.presenceOfElementLocated(favorisIcon));

            // If the heart icon is filled (indicating it's already a favorite), return true
            String favClass = favIcon.getAttribute("class");
            boolean isFavorite = favClass.contains("mdi-heart");  // Check if heart icon is present
            log.info(isFavorite ? "❤️ Lot is already in favorites." : "💔 Lot is NOT in favorites.");
            return isFavorite;
        } catch (Exception e) {
            log.warn("⚠️ Unable to determine if lot is in favorites.");
            return false;
        }
    }

    /**
     * Clicks the "Favoris" button to add the lot to favorites.
     */
    public void addToFavorites() {
        try {
            log.info("🔍 Checking if lot is already in favorites...");

            if (isLotInFavorites()) {
                log.info("✅ Lot is already in favorites. Skipping...");
                return;
            }

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            log.info("🔎 Looking for the 'Favoris' button using XPath: {}");
            List<WebElement> elements = driver.findElements(favorisButton);
            if (elements.isEmpty()) {
                log.error("❌ 'Favoris' button NOT found. XPath may be incorrect!");
                return;
            } else {
                log.info("✅ 'Favoris' button found. Total matches: {}");
            }

            // Get the first button found
            WebElement favButton = wait.until(ExpectedConditions.presenceOfElementLocated(favorisButton));
            wait.until(ExpectedConditions.visibilityOf(favButton));
            wait.until(ExpectedConditions.elementToBeClickable(favButton));

            log.info("✅ 'Favoris' button is visible and clickable.");

            // Scroll into view
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", favButton);
            log.info("📜 Scrolled to 'Favoris' button.");

            // Click the button using JavaScript to ensure execution
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", favButton);
            log.info("🖱️ Clicked on 'Favoris' button using JavaScript.");

            // ✅ Check if heart icon (favorited) is updated
            boolean isFavorited = wait.until(ExpectedConditions.attributeContains(favButton, "class", "mdi-heart"));
            if (isFavorited) {
                log.info("✅ Successfully added to favorites! Heart icon updated.");
            } else {
                log.warn("⚠️ Click action performed, but favorite icon did NOT update.");
            }

        } catch (NoSuchElementException e) {
            log.error("❌ NoSuchElementException: 'Favoris' button not found! XPath might be incorrect.");
        } catch (TimeoutException e) {
            log.error("⏳ TimeoutException: Element did not become clickable within 10 seconds.");
        } catch (WebDriverException e) {
            log.error("❌ WebDriverException: Likely an issue with Selenium or page state - {}");
        } catch (Exception e) {
            log.error("❌ Unexpected error: {}");
        }
    }

}

