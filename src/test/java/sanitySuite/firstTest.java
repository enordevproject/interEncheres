package sanitySuite;

import Models.Lot;
import base.TestBase;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.Home;
import pages.Search;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class firstTest extends TestBase {

    private Home homePage;
    private Search searchPage;

    @BeforeClass
    public void setUp() {
        homePage = new Home(driver);
        searchPage = new Search(driver);
    }

    @Test(priority = 1, description = "Open page, perform search, and print number of lots")
    public void openPageAndSearch() {
        log.info("Navigating to Interencheres home page.");
        homePage.navigateToHomePage(); // Navigate to the home page

        String searchTerm = "dell latitude"; // Search term
        log.info("Performing search for term: " + searchTerm);
        homePage.performSearch(searchTerm); // Perform the search

        // Retrieve the number of lots
        int numberOfLots = searchPage.getNumberOfLots();
        log.info("Number of lots found: " + numberOfLots);
        System.out.println("Number of lots: " + numberOfLots);

        // Retrieve all lots across all pages
        List<Lot> allLots = searchPage.getAllLots();
        log.info("Total lots retrieved: " + allLots.size());
        System.out.println("Total lots retrieved: " + allLots.size());


    }

    @AfterSuite
    public void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.out.println("Error while quitting driver: " + e.getMessage());
            }
        }

        // Kill the ChromeDriver process if it's still running
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
            } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                Runtime.getRuntime().exec("pkill chromedriver");
            }
        } catch (IOException e) {
            System.out.println("Error killing chromedriver process: " + e.getMessage());
        }
    }

}
