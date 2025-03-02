package webApp.services;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import selenium.pages.Home;
import selenium.pages.Search;
import webApp.models.Results;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class LotSearchService {
    private static final Logger log = LoggerFactory.getLogger(LotSearchService.class);
    private final List<String> logMessages = new CopyOnWriteArrayList<>(); // ✅ Stores logs for frontend
    private final AtomicBoolean searchActive = new AtomicBoolean(false);

    private final SeleniumConfigService seleniumConfigService;
    private final ResultsService resultsService;

    public LotSearchService(SeleniumConfigService seleniumConfigService, ResultsService resultsService) {
        this.seleniumConfigService = seleniumConfigService;
        this.resultsService = resultsService;
    }

    /**
     * ✅ Get formatted timestamp for logs (HH:mm:ss)
     */
    private String getFormattedTimestamp() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    /**
     * ✅ Logs messages & adds them to the log list
     */
    private void logMessage(String message) {
        String formattedMessage = "[" + getFormattedTimestamp() + "] " + message; // ✅ Format log message
        log.info(formattedMessage);
        logMessages.add(formattedMessage);
    }

    /**
     * ✅ Fetch logs for frontend
     */
    public List<String> getLogs() {
        return logMessages;
    }

    /**
     * ✅ Performs searches and processes lots. Allows stopping at any time.
     */
    public synchronized boolean performSearchesAndProcessLots(List<String> searchTerms) {
        if (!searchActive.compareAndSet(false, true)) {
            logMessage("⚠️ A search is already running. Please wait.");
            return false;
        }

        WebDriver driver = seleniumConfigService.getDriver();

        try {
            Home homePage = new Home(driver);
            Search searchPage = new Search(driver);

            homePage.navigateToHomePage();
            logMessage("🏠 Navigated to home page.");

            for (String searchTerm : searchTerms) {
                if (!searchActive.get()) {
                    logMessage("⏹️ Search was interrupted.");
                    return false;
                }

                logMessage("🔍 Searching for: " + searchTerm);
                homePage.performSearch(searchTerm);

                if (searchPage.isNoResultsPresent()) {
                    logMessage("⚠️ No results found for: " + searchTerm);
                    continue;
                }

                int numberOfLots = searchPage.getNumberOfLots();
                logMessage("✅ Found " + numberOfLots + " lots for '" + searchTerm + "'");

                Results resultsOnCurrentPage = new Results();
                resultsOnCurrentPage.setLots(searchPage.getLotsOnCurrentPage());
                resultsOnCurrentPage.pushLotsToDatabase(resultsOnCurrentPage.getLots());
                logMessage("📊 Processed " + resultsOnCurrentPage.getLots().size() + " lots.");

                if (searchPage.isPaginationPresent()) {
                    int lastPageNumber = searchPage.getLastPageNumber();
                    logMessage("➡️ Pagination detected. Last page number: " + lastPageNumber);

                    for (int currentPage = 2; currentPage <= lastPageNumber; currentPage++) {
                        if (!searchActive.get()) {
                            logMessage("⏹️ Search interrupted before page " + currentPage);
                            return false;
                        }

                        logMessage("➡️ Navigating to page " + currentPage);
                        searchPage.goToNextPage();
                        searchPage.waitForPageContent();

                        resultsOnCurrentPage.setLots(searchPage.getLotsOnCurrentPage());
                        resultsOnCurrentPage.pushLotsToDatabase(resultsOnCurrentPage.getLots());
                        logMessage("📊 Processed " + resultsOnCurrentPage.getLots().size() + " lots on page " + currentPage);
                    }
                }

                logMessage("✅ Finished processing '" + searchTerm + "'.");
            }
        } catch (Exception e) {
            logMessage("❌ Error: " + e.getMessage());
            return false;
        } finally {
            searchActive.set(false);
            seleniumConfigService.closeDriver();
            logMessage("🛑 WebDriver closed.");
        }

        return true;
    }

    /**
     * ✅ Stops the active search process.
     */
    public void stopSearch() {
        searchActive.set(false);
        seleniumConfigService.closeDriver();
        logMessage("⏹️ Search stopped.");
    }
}
