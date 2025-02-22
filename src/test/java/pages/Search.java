package pages;


import Models.Lot;
import org.openqa.selenium.*;
import base.BasePage;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

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
    private final By AUCTION_URL_XPATH = By.xpath("//a[@class='d-flex fill-height']");


    // Méthode pour récupérer les lots sur la page actuelle


    public int getNumberOfLots() {
        WebElement counterElement = driver.findElement(lotsNumber);
        String lotText = counterElement.getText().trim();
        // Retirer les espaces insécables et autres caractères non numériques
        String cleanText = lotText.replaceAll("[^0-9]", "");
        return Integer.parseInt(cleanText);
    }

    public void selectCountry(String country) {
        // Clique sur le conteneur du sélecteur
        WebElement selectElement = driver.findElement(countrySelect);
        selectElement.click();

        // Attendre que la liste soit visible (facultatif, selon le besoin)
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='v-list-item__title' and text()='" + country + "']")));

        // Clique sur l'élément de la liste correspondant au pays
        WebElement countryElement = driver.findElement(By.xpath("//div[@class='v-list-item__title' and text()='" + country + "']"));
        countryElement.click();
    }

    // Méthode pour récupérer la date du lot
    public String getLotDate() {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement lotDateElement = wait.until(ExpectedConditions.visibilityOfElementLocated(LOT_DATE_XPATH));
        return lotDateElement.getText().trim();
    }

    // Méthode pour récupérer les lots sur la page actuelle
    public List<Lot> getLotsOnCurrentPage() {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(LOT_ITEMS_XPATH));

        List<WebElement> lotElements = driver.findElements(LOT_ITEMS_XPATH);
        List<Lot> lots = new ArrayList<>();

        for (WebElement lotElement : lotElements) {
            try {
                // Initialize default values
                String lotNumber = "";
                String description = "";
                String estimationPrice = "";
                String date = "";
                String auctionHouse = "";
                String imgUrl = "";
                String url = "";

                // Extract Lot Number (Handle missing or malformed)
                try {
                    lotNumber = lotElement.findElement(LOT_NUMBER_XPATH).getText().trim();

                } catch (Exception e) {
                    lotNumber = "No lot available"; // Default if missing
                }

                // Extract Description
                try {
                    description = lotElement.findElement(LOT_DESCRIPTION_XPATH).getText().trim();
                } catch (Exception e) {
                    description = "No description available"; // Default if missing
                }

                // Extract Estimation Price
                try {
                    estimationPrice = lotElement.findElement(LOT_ESTIMATION_XPATH).getText().trim();
                } catch (Exception e) {
                    estimationPrice = "No estimation available"; // Default if missing
                }

                // Extract Date
                try {
                    date = lotElement.findElement(LOT_DATE_XPATH).getText().trim();
                } catch (Exception e) {
                    date = "No date available"; // Default if missing
                }

                // Extract Auction House
                try {
                    auctionHouse = lotElement.findElement(LOT_AUCTION_HOUSE_XPATH).getText().trim();
                } catch (Exception e) {
                    auctionHouse = "No auction house available"; // Default if missing
                }

                // Extract Image URL
                try {
                    imgUrl = lotElement.findElement(IMG_URL_XPATH).getAttribute("src").trim();
                } catch (Exception e) {
                    imgUrl = "No image available"; // Default if missing
                }

                // Extract URL
                try {
                    url = lotElement.findElement(AUCTION_URL_XPATH).getAttribute("href").trim();
                } catch (Exception e) {
                    url = "No URL available"; // Default if missing
                }

                // Create a Lot object and add to the list
                Lot lot = new Lot();
                lot.setNumber(lotNumber);
                lot.setDescription(description);
                lot.setEstimationPrice(estimationPrice);
                lot.setDate(date);
                lot.setMaisonEnchere(auctionHouse);
                lot.setImgUrl(imgUrl);
                lot.setUrl(url);

                lots.add(lot);

            } catch (Exception e) {
                e.printStackTrace(); // Handle any unexpected errors
            }
        }

        return lots;
    }



    public List<Lot> getAllLots() {
        List<Lot> allLots = new ArrayList<>();

        // Process the first page
        allLots.addAll(getLotsOnCurrentPage());

        // Get the last page number to determine how many pages to loop through
        int lastPageNumber = getLastPageNumber();

        for (int currentPage = 2; currentPage <= lastPageNumber; currentPage++) {
            // Go to next page
            goToNextPage();
            // Wait for the next page's content to load
            waitForPageContent();
            // Collect lots from the new page
            allLots.addAll(getLotsOnCurrentPage());
        }

        return allLots;
    }

    // Method to get the last page number from the pagination
    // Improved XPath to specifically target the last page button (without disabled classes)
    private int getLastPageNumber() {
        WebDriverWait wait = new WebDriverWait(driver, 2);

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
        WebDriverWait wait = new WebDriverWait(driver, 2);
        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(NEXT_PAGE_BUTTON_XPATH));
        nextButton.click();
        waitForPageContent();
    }

    // Explicit wait for page content to load
    private void waitForPageContent() {
        WebDriverWait wait = new WebDriverWait(driver, 2);
        wait.until(ExpectedConditions.presenceOfElementLocated(LOT_ITEMS_XPATH));
    }













}
