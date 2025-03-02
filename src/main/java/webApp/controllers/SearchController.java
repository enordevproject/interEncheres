package webApp.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webApp.services.LotSearchService;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/search")
public class SearchController {

    private static final Logger log = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private LotSearchService searchService;

    private static final ConcurrentHashMap<String, Boolean> activeSearches = new ConcurrentHashMap<>();
    private static final AtomicBoolean searchActive = new AtomicBoolean(false); // ✅ Track search status

    /**
     * ✅ Executes a search request (one at a time).
     */
    @PostMapping("/execute")
    public ResponseEntity<String> executeSearch(@RequestBody List<String> keywords) {
        log.info("🔍 Received search request with keywords: {}", keywords);

        // ✅ Prevent duplicate requests
        if (!searchActive.compareAndSet(false, true)) { // ✅ Ensures only one search runs at a time
            log.warn("⚠️ A search is already running. Please wait.");
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("⚠️ A search is already in progress. Please wait.");
        }

        try {
            log.info("🚀 Starting search execution...");
            boolean success = searchService.performSearchesAndProcessLots(keywords);

            if (!success) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("⚠️ Search was interrupted.");
            }

            log.info("✅ Search execution completed successfully.");
            return ResponseEntity.ok("Search executed successfully.");
        } catch (Exception e) {
            log.error("❌ Search execution failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to execute search: " + e.getMessage());
        } finally {
            searchActive.set(false); // ✅ Ensure the flag resets only when the search truly finishes
        }
    }

    /**
     * ✅ Stops an active search process.
     */
    @CrossOrigin(origins = "*") // ✅ Fixes CORS issue

    @PostMapping("/stop")
    public ResponseEntity<String> stopSearch() {
        log.info("⏹️ Stopping search...");
        searchService.stopSearch(); // ✅ Stop WebDriver & active search
        searchActive.set(false);
        return ResponseEntity.ok("✅ Search stopped.");
    }
    @CrossOrigin(origins = "*") // ✅ Fixes CORS issue
    @GetMapping("/logs")
    public ResponseEntity<List<String>> getLogs() {
        return ResponseEntity.ok(searchService.getLogs());
    }
}
