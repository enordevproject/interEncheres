package webApp.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import webApp.services.LotSearchService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/search")
public class SearchController {

    private static final Logger log = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private LotSearchService searchService;

    /**
     * Executes an asynchronous search with a longer timeout to prevent 503 errors.
     * Uses DeferredResult to keep the request open.
     */
    @PostMapping("/execute")
    public DeferredResult<ResponseEntity<String>> executeSearch(@RequestBody List<String> keywords) {
        log.info("🔍 Received search request with keywords: {}", keywords);

        // Set timeout to 60 seconds (60000 ms)
        DeferredResult<ResponseEntity<String>> deferredResult = new DeferredResult<>(60000L);

        CompletableFuture.runAsync(() -> {
            try {
                log.info("🚀 Starting search execution...");
                searchService.performSearchesAndProcessLots(keywords).join(); // Ensures async execution completes
                log.info("✅ Search execution completed successfully.");
                deferredResult.setResult(ResponseEntity.ok("Search executed successfully."));
            } catch (Exception e) {
                log.error("❌ Search execution failed: {}", e.getMessage(), e);
                deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to execute search: " + e.getMessage()));
            }
        });

        // If the timeout expires before completing, return a proper error message
        deferredResult.onTimeout(() -> {
            log.warn("⏳ Search request timed out.");
            deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("⚠️ Request timed out. Please try again with fewer keywords."));
        });

        return deferredResult;
    }
}
