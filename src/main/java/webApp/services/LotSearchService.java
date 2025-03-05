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
    private final List<String> logMessages = new CopyOnWriteArrayList<>();
    private final AtomicBoolean searchActive = new AtomicBoolean(false);

    private final SeleniumConfigService seleniumConfigService;
    private final ResultsService resultsService;

    public LotSearchService(SeleniumConfigService seleniumConfigService, ResultsService resultsService) {
        this.seleniumConfigService = seleniumConfigService;
        this.resultsService = resultsService;
    }

    private String getFormattedTimestamp() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    private void logMessage(String message) {
        String formattedMessage = "[" + getFormattedTimestamp() + "] " + message;
        log.info(formattedMessage);
        logMessages.add(formattedMessage);
    }

    public List<String> getLogs() {
        return logMessages;
    }

    public synchronized boolean performSearchesAndProcessLots(List<String> searchTerms) {
        if (!searchActive.compareAndSet(false, true)) {
            logMessage("⚠️ A search is already running. Please wait.");
            return false;
        }

        WebDriver driver = initializeWebDriver();
        if (driver == null) {
            logMessage("❌ WebDriver failed to initialize! Aborting search.");
            searchActive.set(false);
            return false;
        }

        try {
            Home homePage = new Home(driver);
            Search searchPage = new Search(driver);

            logMessage("🏠 Navigating to home page...");
            homePage.navigateToHomePage();
            logMessage("✅ Successfully reached the home page.");

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
            logMessage("❌ Error during search execution: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            searchActive.set(false);
            seleniumConfigService.closeDriver();
            logMessage("🛑 WebDriver closed.");
        }

        return true;
    }

    /**
     * ✅ Attempts to initialize WebDriver up to 3 times if it fails.
     */
    private WebDriver initializeWebDriver() {
        int retries = 3;
        for (int attempt = 1; attempt <= retries; attempt++) {
            logMessage("🔄 Attempt " + attempt + " to initialize WebDriver...");
            WebDriver driver = seleniumConfigService.getDriver();

            if (driver != null) {
                logMessage("✅ WebDriver initialized successfully.");
                return driver;
            }

            logMessage("❌ WebDriver initialization failed. Retrying...");



            try {
                Thread.sleep(2000); // ✅ Small delay before retrying
            } catch (InterruptedException ignored) {
            }
        }

        logMessage("❌ All attempts to initialize WebDriver failed.");
        return null;
    }

    public void stopSearch() {
        searchActive.set(false);
        seleniumConfigService.closeDriver();
        logMessage("⏹️ Search stopped.");
    }
}
