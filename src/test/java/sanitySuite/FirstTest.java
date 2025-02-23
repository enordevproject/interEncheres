package sanitySuite;

import Models.Lot;
import Models.Results;
import base.BasePage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.Home;
import pages.Search;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public  class FirstTest extends BasePage {
    private static final Logger log = LoggerFactory.getLogger(FirstTest.class);
    private Home homePage;
    private Search searchPage;

    @BeforeClass
    public void setUp() {

        homePage = new Home(driver);
        searchPage = new Search(driver);
    }


    @Test(priority = 1, description = "Open page, perform search for multiple terms, and insert lots into the database")
    public void openPageAndSearch() {
        log.info("Navigating to Interencheres home page.");
        homePage.navigateToHomePage(); // Navigate to the home page

        // Define search terms
        List<String> searchTerms = Arrays.asList("ordinateur portable", "QSsQS", "dell", "QSsQS", "QSsQS", "QSsQS"); // Use Arrays.asList()

        // Loop through each search term
        for (String searchTerm : searchTerms) {
            log.info("Performing search for term: {}", searchTerm);
            homePage.performSearch(searchTerm); // Perform the search for the current term

            // Check if no results are found
            if (searchPage.isNoResultsPresent()) {
                log.info("No results found for search term: {}", searchTerm);
                continue; // Skip further processing if no results are found
            }

            int numberOfLots = searchPage.getNumberOfLots();
            log.info("Number of lots found for search term '{}': {}", searchTerm, numberOfLots);

            Results resultsOnCurrentPage = new Results();

            // Add lots from the first page
            resultsOnCurrentPage.setLots(searchPage.getLotsOnCurrentPage());
            resultsOnCurrentPage.pushLotsToDatabase(resultsOnCurrentPage.getLots());
            resultsOnCurrentPage.getLots().clear();

            // Check for pagination and process additional pages
            if (searchPage.isPaginationPresent()) {
                int lastPageNumber = searchPage.getLastPageNumber();
                log.info("Pagination detected. Last page number: {}", lastPageNumber);

                for (int currentPage = 2; currentPage <= lastPageNumber; currentPage++) {
                    log.info("Navigating to page {}.", currentPage);
                    // Navigate to next page and wait for content
                    searchPage.goToNextPage();
                    searchPage.waitForPageContent();

                    // Add lots from the current page
                    resultsOnCurrentPage.setLots(searchPage.getLotsOnCurrentPage());
                    resultsOnCurrentPage.pushLotsToDatabase(resultsOnCurrentPage.getLots());
                    resultsOnCurrentPage.getLots().clear();
                }
            }
        }

        log.info("All search terms processed, and lots have been pushed to the database.");
    }


    @AfterSuite
    public void tearDown() {
        if (driver != null) {
            try {
                BasePage.quitDriver(); // Using the quitDriver method from BasePage to quit the driver
                log.info("Driver successfully quit.");
            } catch (Exception e) {
                log.error("Error while quitting driver: {}", e.getMessage());
            }
        }

        // Kill the ChromeDriver process if it's still running
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
                log.info("ChromeDriver process terminated on Windows.");
            } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                Runtime.getRuntime().exec("pkill chromedriver");
                log.info("ChromeDriver process terminated on Unix-like OS.");
            }
        } catch (IOException e) {
            log.error("Error killing chromedriver process: {}", e.getMessage());
        }
    }
}
