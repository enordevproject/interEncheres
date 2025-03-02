package selenium.pages;

import selenium.base.BasePage;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import webApp.models.Lot;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Search extends BasePage {

    public Search(WebDriver driver) {
        super(driver);
    }

    private By lotsButton = By.xpath("//a[contains(@href, '/recherche/lots?search=') and contains(@class, 'v-tab')]");
    private By lotsNumber = By.xpath("//a[contains(@href, '/recherche/lots?search=') and contains(@class, 'v-tab')]/div");


    //filters

    //localisation
    private By locationButton = By.xpath("//button[contains(@class, 'v-btn') and .//span[contains(text(), 'Localisation')]]");

    private By countrySelect = By.xpath("(//div[@class='v-select__slot'])[4]");


    // XPath pour les éléments de la page
    private final By LOT_DATE_XPATH = By.xpath("//div[@class='bottom d-flex flex-wrap full-width align-self-end align-center justify-space-between px-2']//div[@class='text-caption font-weight-medium']//span");
    private final By LOT_ITEMS_XPATH = By.xpath("//div[contains(@class, 'pa-1') and contains(@class, 'col-md-4')]\n"); // Remplacez par le bon XPath pour les lots
    private final By NEXT_PAGE_BUTTON_XPATH = By.xpath("//button[@aria-label='Page suivante']");
    private final By PAGINATION_DISABLED_CLASS = By.xpath("//button[@aria-label='Page suivante' and contains(@class, 'v-pagination__navigation--disabled')]");


    // XPath pour les éléments des lots

    // XPath pour les informations spécifiques de chaque lot
    private final By LOT_NUMBER_XPATH = By.xpath(".//span[@class='font-weight-bold']");
    private final By LOT_DESCRIPTION_XPATH = By.xpath(".//div[contains(@class, 'description')]");
    private final By LOT_ESTIMATION_XPATH = By.xpath("//div[@class='estimates d-flex flex-wrap justify-center']//span[@class='text-pre-wrap flex-shrink-0 mx-1 text-center']");
    private final By LOT_AUCTION_HOUSE_XPATH = By.xpath("//div[@class='pa-1 col-md-4 col-lg-3 col-6 pa-0']//div[@class='organization-name text-caption font-weight-medium text_primary--text pt-2 px-2']//span[2]");
    private final By IMG_URL_XPATH = By.xpath("//div[@class='v-responsive__content d-flex align-center justify-center']/img");
    private final By AUCTION_URL_XPATH = By.xpath(".//a[@class='d-flex fill-height']");

    private By noResultsMessage = By.xpath("//p[contains(text(), 'Aucun lot ne correspond à cette demande')]");

    // Méthode pour récupérer les lots sur la page actuelle


    public int getNumberOfLots() {
        WebElement counterElement = driver.findElement(lotsNumber);
        String lotText = counterElement.getText().trim();
        // Retirer les espaces insécables et autres caractères non numériques
        String cleanText = lotText.replaceAll("[^0-9]", "");
        return Integer.parseInt(cleanText);
    }



    public List<Lot> getLotsOnCurrentPage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(LOT_ITEMS_XPATH));

        List<WebElement> lotElements = driver.findElements(LOT_ITEMS_XPATH);
        List<Lot> lots = new ArrayList<>();

      //  log.info("🔍 Extracting lots on the current page. Found {} lot elements.", lotElements.size());

        for (WebElement lotElement : lotElements) {
            try {
                String url = extractUrl(lotElement);
                if (url == null) continue; // Skip if no URL

                Lot lot = new Lot();
                lot.setNumber(extractData(lotElement, LOT_NUMBER_XPATH, "No lot available"));
                lot.setDescription(extractData(lotElement, LOT_DESCRIPTION_XPATH, "No description available"));
                lot.setEstimationPrice(extractData(lotElement, LOT_ESTIMATION_XPATH, "No estimation available"));
                lot.setDate(extractData(lotElement, LOT_DATE_XPATH, "No date available"));
                lot.setMaisonEnchere(extractData(lotElement, LOT_AUCTION_HOUSE_XPATH, "No auction house available"));

                // ✅ Extract the correct image for this lot inside the loop
                lot.setImgUrl(extractImgData(lotElement));

                lot.setUrl(url);
                lot.setInsertionDate(LocalDateTime.now());

                lots.add(lot);

              //  log.info("✅ Lot extracted: {} | Image URL: {}", url, lot.getImgUrl()); // Debugging logs
            } catch (Exception e) {
                log.error("❌ Error processing lot: ", e);
            }
        }

       // log.info("✅ Finished extracting lots. Total lots extracted: {}", lots.size());
        return lots;
    }






    // Helper method to extract a URL from a lot element
    private String extractUrl(WebElement lotElement) {
        try {
            String relativeUrl = lotElement.findElement(AUCTION_URL_XPATH).getAttribute("href").trim();
            if (!relativeUrl.startsWith("http")) {
                String baseUrl = "https://www.interencheres.com";
                return baseUrl + relativeUrl;
            } else {
                return relativeUrl;
            }
        } catch (Exception e) {
            log.warn("URL extraction failed.");
            return null; // Return null if URL is missing
        }
    }

    // Helper method to extract data with a default value
    private String extractData(WebElement lotElement, By xpath, String defaultValue) {
        try {
            return lotElement.findElement(xpath).getText().trim();
        } catch (Exception e) {
            return defaultValue; // Return the default value if extraction fails
        }
    }
    private String extractImgData(WebElement lotElement) {
        try {
            // 🔴 Locate the image inside the specific `lotElement`
            WebElement imgElement = lotElement.findElement(By.xpath(".//div[@class='v-responsive__content d-flex align-center justify-center']/img"));

            // Extract the image URL
            String imgUrl = imgElement.getAttribute("src");

            // Convert relative URL to absolute
            if (imgUrl != null && imgUrl.startsWith("//")) {
                imgUrl = "https:" + imgUrl;
            }

            return imgUrl;
        } catch (NoSuchElementException e) {
            log.warn("⚠️ No image found for lot.");
            return "No image available";
        } catch (StaleElementReferenceException e) {
            log.warn("⚠️ StaleElementReferenceException: Retrying image extraction...");
            return extractImgData(lotElement);
        } catch (Exception e) {
            log.warn("⚠️ Image extraction failed.");
            return "No image available";
        }
    }



    // Method to check if there are no results
    public boolean isNoResultsPresent() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
            // Check for the no-results message using the 'noResultsMessage' By variable
            WebElement noResultsMessageElement = wait.until(ExpectedConditions.presenceOfElementLocated(noResultsMessage));
            return noResultsMessageElement != null;
        } catch (Exception e) {
            // If the message is not found, no results are present
            return false;
        }
    }

    // Method to check if pagination is present
    public boolean isPaginationPresent() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
            // Try to find the pagination element
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".v-pagination")));
            return true;
        } catch (Exception e) {
            // If pagination is not found, return false
            return false;
        }
    }

    // Method to get the last page number from the pagination
    public int getLastPageNumber() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));

        // Find the last page number in the pagination (not disabled)
        WebElement lastPageButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("(//button[@aria-label and contains(@class, 'v-pagination__item') and not(contains(@class, 'v-pagination__navigation--disabled'))])[last()]")
        ));

        // Extract the page number from the button's aria-label attribute (e.g., 'Aller à la page 12')
        String lastPageNumberText = lastPageButton.getAttribute("aria-label").replaceAll("[^0-9]", "").trim();
        return Integer.parseInt(lastPageNumberText);
    }


    // Modified goToNextPage method to directly click the next button
    public void goToNextPage() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // ✅ Wait until the "Next" button is clickable
            WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@aria-label='Page suivante']")));

            log.info("🖱️ Clicking the 'Next Page' button...");
            nextButton.click();

            // ✅ Wait for the page to fully load before proceeding
            waitForPageContent();
        } catch (Exception e) {
            log.warn("⚠️ Failed to click 'Next Page' button: {}");
        }
    }


    // Explicit wait for page content to load
    public void waitForPageContent() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(ExpectedConditions.presenceOfElementLocated(LOT_ITEMS_XPATH));
    }













}