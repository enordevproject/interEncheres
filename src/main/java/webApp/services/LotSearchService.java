package webApp.services;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import selenium.pages.Home;
import selenium.pages.Search;
import webApp.models.Lot;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class LotSearchService {

    @Autowired
    private SeleniumConfigService seleniumConfigService;

    @Autowired
    private ResultsService resultsService;

    /**
     * Asynchronously performs searches and processes lots based on the given search terms.
     *
     * @param searchTerms List of keywords for which to perform searches.
     */
    @Async
    public CompletableFuture<Void> performSearchesAndProcessLots(List<String> searchTerms) {
        WebDriver driver = seleniumConfigService.getDriver();

        try {
            Home homePage = new Home(driver);
            Search searchPage = new Search(driver);

            homePage.navigateToHomePage();

            for (String searchTerm : searchTerms) {
                homePage.performSearch(searchTerm); // Perform the search for the current term

                if (searchPage.isNoResultsPresent()) {
                    System.out.println("No results found for search term: " + searchTerm);
                    continue; // Skip further processing if no results are found
                }

                processCurrentAndSubsequentPages(searchPage, driver);
            }
        } finally {
            seleniumConfigService.closeDriver(); // Properly manage the WebDriver lifecycle
        }

        return CompletableFuture.completedFuture(null);
    }

    /**
     * Processes the current and all subsequent pages if pagination is present.
     *
     * @param searchPage The search page handler.
     * @param driver The WebDriver instance for page navigation.
     */
    private void processCurrentAndSubsequentPages(Search searchPage, WebDriver driver) {
        do {
            List<Lot> lots = searchPage.getLotsOnCurrentPage();
            if (!lots.isEmpty()) {
                resultsService.processAndStoreLots(lots);
            }

            if (searchPage.isPaginationPresent()) {
                searchPage.goToNextPage();
                searchPage.waitForPageContent(); // Ensure the new page has loaded before continuing
            } else {
                break; // Exit the loop if no more pages are present
            }
        } while (true);
    }
}
