package webApp.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webApp.services.LotSearchService;
import webApp.services.LotService;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/search")
public class SearchController {
    private static final Logger log = LoggerFactory.getLogger(SearchController.class);

    private final LotSearchService searchService;
    private final LotService lotService;
    private final AtomicBoolean searchActive = new AtomicBoolean(false);
    private final AtomicBoolean processingLots = new AtomicBoolean(false); // ✅ Prevents duplicate lot processing

    public SearchController(LotSearchService searchService, LotService lotService) {
        this.searchService = searchService;
        this.lotService = lotService;
    }

    /**
     * ✅ Start the search process.
     */
    @CrossOrigin(origins = "*")
    @PostMapping("/execute")
    public ResponseEntity<String> executeSearch(@RequestBody List<String> keywords) {
        log.info("🔍 Received search request with keywords: {}", keywords);

        // ✅ If a search is already running, return a message instead of blocking
        if (searchActive.get()) {
            log.warn("⚠️ A search is already in progress.");
            return ResponseEntity.ok("⚠️ A search is already running. Please wait.");
        }

        searchActive.set(true); // ✅ Mark search as active

        new Thread(() -> {
            try {
                log.info("🚀 Starting Selenium search execution...");

                boolean success = searchService.performSearchesAndProcessLots(keywords);

                if (success) {
                    log.info("✅ Search completed successfully.");
                } else {
                    log.warn("⚠️ Search completed, but no results were found.");
                }

                log.info("✅ Checking if lot processing is needed...");
                if (processingLots.compareAndSet(false, true)) {
                    try {
                        lotService.processLotsWithGPT();
                        log.info("✅ Lots processed successfully.");
                    } catch (Exception e) {
                        log.error("❌ Error processing lots: {}", e.getMessage());
                    } finally {
                        processingLots.set(false); // ✅ Reset processing flag
                    }
                } else {
                    log.warn("⚠️ Lot processing is already in progress. Skipping duplicate execution.");
                }
            } catch (Exception e) {
                log.error("❌ Search execution failed: {}", e.getMessage(), e);
            } finally {
                searchActive.set(false); // ✅ Ensure the search can be triggered again
            }
        }).start();

        return ResponseEntity.ok("✅ Search started.");
    }

    /**
     * ✅ Stop an active search.
     */
    @CrossOrigin(origins = "*")
    @PostMapping("/stop")
    public ResponseEntity<String> stopSearch() {
        if (!searchActive.get()) {
            return ResponseEntity.ok("✅ No active search to stop.");
        }

        log.info("⏹️ Stopping search...");
        searchService.stopSearch();
        searchActive.set(false);

        log.info("✅ Search stopped. Initiating lot processing...");
        try {
            lotService.processLotsWithGPT();
        } catch (Exception e) {
            log.error("❌ Error processing lots after stopping search: {}", e.getMessage());
        }

        return ResponseEntity.ok("✅ Search stopped.");
    }

    /**
     * ✅ Fetch logs from both search and GPT processing
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/logs")
    public ResponseEntity<List<String>> getLogs() {
        List<String> logs = searchService.getLogs();
        logs.addAll(lotService.getLogs()); // ✅ Combine search and GPT logs
        return ResponseEntity.ok(logs);
    }
}
