package sanitySuite;

import Models.Lot;
import base.TestBase;
import hibernate.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.Home;
import pages.Search;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class firstTest extends TestBase {

    private Home homePage;
    private Search searchPage;

    @BeforeClass
    public void setUp() {
        homePage = new Home(driver);
        searchPage = new Search(driver);
    }
    // Open session from HibernateUtil
    Session session = HibernateUtil.getSessionFactory().openSession();
    Transaction tx = session.beginTransaction();
    @Test(priority = 1, description = "Open page, perform search for multiple terms, and insert lots into the database")
    public void openPageAndSearch() {
        log.info("Navigating to Interencheres home page.");
        homePage.navigateToHomePage(); // Navigate to the home page

        // Define search terms using ArrayList (for older Java versions)
        List<String> searchTerms = new ArrayList<>();

        searchTerms.add("hp elitebook");
        searchTerms.add("lenovo thinkpad");
        searchTerms.add("vente judiciaire ordinateur");
        searchTerms.add("saisie ordinateur");
        searchTerms.add("liquidation judiciaire pc");
        searchTerms.add("vente aux enchères ordinateur");
        searchTerms.add("vente judiciaire informatique");
        searchTerms.add("vente saisie ordinateur");
        searchTerms.add("saisie pc portable");
        searchTerms.add("actif judiciaire ordinateur");
        searchTerms.add("enchères judiciaires pc");
        searchTerms.add("juge liquidation ordinateur");
        searchTerms.add("saisie biens informatique pc");
        searchTerms.add("liquidation judiciaire informatique");
        searchTerms.add("vente judiciaire matériel informatique");
        searchTerms.add("décision judiciaire pc");
        searchTerms.add("vente aux enchères pc portable");
        searchTerms.add("ordinateur portable");
        searchTerms.add("pc portable");
        searchTerms.add("dell");
        searchTerms.add("hp");
        searchTerms.add("lenovo");
        searchTerms.add("macbook");
        searchTerms.add("ultrabook");
        searchTerms.add("laptop");
        searchTerms.add("gaming pc");
        searchTerms.add("workstation");
        searchTerms.add("ordinateur professionnel");
        searchTerms.add("pc reconditionné");
        searchTerms.add("ordinateur d'occasion");
        searchTerms.add("pc de bureau");
        searchTerms.add("ordinateur mac");

        // Loop through each search term
        for (String searchTerm : searchTerms) {
            log.info("Performing search for term: " + searchTerm);
            homePage.performSearch(searchTerm); // Perform the search for the current term

            // Retrieve the number of lots and all lots across all pages
            int numberOfLots = searchPage.getNumberOfLots();
            log.info("Number of lots found for search term '" + searchTerm + "': " + numberOfLots);
            System.out.println("Number of lots found for '" + searchTerm + "': " + numberOfLots);

            List<Lot> allLots = searchPage.getAllLots(); // Get all the lots for the current search term
            long addedCount = allLots.stream()
                    .filter(lot -> !checkIfUrlExists(lot.getUrl())) // Filter out existing URLs
                    .peek(lot -> {
                        // If a lot with the same ID exists, merge the new lot.
                        session.merge(lot); // Save or update the lot
                        System.out.println("Lot with URL: " + lot.getUrl() + " has been successfully added to the database.");
                    })
                    .count(); // Count the added lots

            // Log and print the total number of added lots for the current search term
            log.info("Total lots added for search term '" + searchTerm + "': " + addedCount);
            System.out.println("Total lots added for search term '" + searchTerm + "': " + addedCount);
        }

        // Flush the session and commit the transaction after processing all search terms
        session.flush();
        tx.commit(); // Commit the transaction
        session.close(); // Close the session
    }





    // Method to check if the URL already exists in the database
    private boolean checkIfUrlExists(String url) {
        String hql = "SELECT COUNT(*) FROM Lot l1_0 WHERE l1_0.url = :url"; // Assuming this is correct
        Long count = (Long) session.createQuery(hql)
                .setParameter("url", url)
                .uniqueResult();
        return count > 0; // Return true if the URL already exists
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
