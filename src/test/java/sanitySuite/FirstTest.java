package sanitySuite;

import Models.Laptop;
import Models.Lot;
import Models.Results;
import Models.GPTService;

import base.BasePage;
import hibernate.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import pages.Home;
import pages.Search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FirstTest extends BasePage {
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
        List<String> searchTerms = Arrays.asList("portable","dell","lenovo","hp"); // Use Arrays.asList()

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
    @Test(priority = 2, description = "Retrieve lots, send them to GPT-4, and store laptops in the database in parallel")
    public void processLotsWithGPT() throws IOException {
        log.info("🔄 [Start] Processing lots with GPT-4...");

        List<Lot> lotsFromDatabase = Results.getAllLotsFromDatabase();
        if (lotsFromDatabase.isEmpty()) {
            log.warn("⚠️ No lots found in the database.");
            return;
        }

        log.info("✅ Retrieved {} lots.", lotsFromDatabase.size());

        // ✅ Create a thread pool with 10 parallel tasks
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        List<Future<Void>> futures = new ArrayList<>();

        for (Lot lot : lotsFromDatabase) {
            futures.add(executorService.submit(() -> {
                Results.processLot(lot);
                return null;
            }));
        }

        // ✅ Wait for all tasks to complete
        for (Future<Void> future : futures) {
            try {
                future.get(); // Ensure each task completes
            } catch (Exception e) {
                log.error("❌ Error processing a lot: {}", e.getMessage(), e);
            }
        }

        // ✅ Shutdown executor
        executorService.shutdown();

        log.info("✅ [Finish] Processing complete.");
    }



    @Test(priority = 3, description = "Generate an HTML report of laptops from the database")
    public void generateLaptopHTMLReport() {
        System.out.println("🔄 Generating laptops report...");
        Results.generateHtmlReport();
        System.out.println("✅ Laptops report successfully generated!");
    }
    @AfterSuite
    public void tearDown() {
        if (driver != null) {
            try {
                BasePage.quitDriver();
                log.info("🚪 Driver successfully closed.");
            } catch (Exception e) {
                log.error("❌ Error closing driver: {}", e.getMessage());
            }
        }

        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
                log.info("🛑 ChromeDriver terminated on Windows.");
            } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                Runtime.getRuntime().exec("pkill chromedriver");
                log.info("🛑 ChromeDriver terminated on Unix-like OS.");
            }
        } catch (IOException e) {
            log.error("❌ Error terminating ChromeDriver process: {}", e.getMessage());
        }
    }
}
