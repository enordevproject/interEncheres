package webApp.services;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import selenium.pages.Home;
import selenium.pages.Search;
import webApp.models.Results;
import java.util.List;

@Service
public class LotSearchService {
    private static final Logger log = LoggerFactory.getLogger(LotSearchService.class);

    @Autowired
    private SeleniumConfigService seleniumConfigService; // ✅ Singleton WebDriver

    @Autowired
    private ResultsService resultsService;

    /**
     * ✅ Ensures searches are executed **one at a time**.
     */
    public synchronized void performSearchesAndProcessLots(List<String> searchTerms) {
        WebDriver driver = seleniumConfigService.getDriver(); // ✅ Singleton WebDriver

        try {
            Home homePage = new Home(driver);
            Search searchPage = new Search(driver);

            homePage.navigateToHomePage();

            for (String searchTerm : searchTerms) {
                log.info("🔍 Performing search for term: {}", searchTerm);

                // ✅ Clear the search input before starting a new search
              //  homePage.clearSearchField();
                homePage.performSearch(searchTerm);

                // ✅ Check if no results are found
                if (searchPage.isNoResultsPresent()) {
                    log.info("⚠️ No results found for search term: {}", searchTerm);
                    continue; // ✅ Skip further processing if no results
                }

                int numberOfLots = searchPage.getNumberOfLots();
                log.info("✅ Number of lots found for search term '{}': {}", searchTerm, numberOfLots);

                Results resultsOnCurrentPage = new Results();

                // ✅ Process first page
                resultsOnCurrentPage.setLots(searchPage.getLotsOnCurrentPage());
                resultsOnCurrentPage.pushLotsToDatabase(resultsOnCurrentPage.getLots());
                resultsOnCurrentPage.getLots().clear();

                // ✅ Process pagination if available
                if (searchPage.isPaginationPresent()) {
                    int lastPageNumber = searchPage.getLastPageNumber();
                    log.info("➡️ Pagination detected. Last page number: {}", lastPageNumber);

                    for (int currentPage = 2; currentPage <= lastPageNumber; currentPage++) {
                        log.info("➡️ Navigating to page {}.", currentPage);

                        searchPage.goToNextPage();
                        searchPage.waitForPageContent();

                        // ✅ Process current page
                        resultsOnCurrentPage.setLots(searchPage.getLotsOnCurrentPage());
                        resultsOnCurrentPage.pushLotsToDatabase(resultsOnCurrentPage.getLots());
                        resultsOnCurrentPage.getLots().clear();
                    }
                }

                log.info("✅ Finished processing '{}' and all its pages. Moving to next keyword...", searchTerm);
            }
        } catch (Exception e) {
            log.error("❌ Error during search execution: {}", e.getMessage(), e);
        } finally {
            seleniumConfigService.closeDriver(); // ✅ Ensures WebDriver closes after execution
        }
    }
}
