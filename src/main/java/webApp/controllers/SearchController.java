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

        // ✅ If this is the first search, allow execution without checking searchActive
        if (!searchActive.get()) {
            searchActive.set(true); // ✅ Mark search as active

            new Thread(() -> {
                boolean success = searchService.performSearchesAndProcessLots(keywords);
                searchActive.set(false);

                if (success) {
                    log.info("✅ Search completed. Checking if lot processing is needed...");

                    // ✅ Ensure lots are only processed ONCE after search ends
                    if (processingLots.compareAndSet(false, true)) {
                        try {
                            lotService.processLotsWithGPT();
                        } catch (Exception e) {
                            log.error("❌ Error processing lots: {}", e.getMessage());
                        } finally {
                            processingLots.set(false); // ✅ Reset processing flag
                        }
                    } else {
                        log.warn("⚠️ Lot processing is already in progress. Skipping duplicate execution.");
                    }
                }
            }).start();

            return ResponseEntity.ok("✅ Search started.");
        }

        // ✅ If search is already running, do NOT return a conflict
        log.warn("⚠️ A search is already in progress.");
        return ResponseEntity.ok("⚠️ A search is already running. Please wait.");
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
    @GetMapping("/logs")
    public ResponseEntity<List<String>> getLogs() {
        List<String> logs = searchService.getLogs();
        logs.addAll(lotService.getLogs()); // ✅ Combine search and GPT logs
        return ResponseEntity.ok(logs);
    }
}
