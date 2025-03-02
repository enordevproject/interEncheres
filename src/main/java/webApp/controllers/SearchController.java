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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/search")
public class SearchController {

    private static final Logger log = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private LotSearchService searchService;

    private static final ConcurrentHashMap<String, Boolean> activeSearches = new ConcurrentHashMap<>();

    /**
     * ✅ Executes a **synchronous** search request (one at a time).
     */
    @PostMapping("/execute")
    public ResponseEntity<String> executeSearch(@RequestBody List<String> keywords) {
        log.info("🔍 Received search request with keywords: {}", keywords);

        // ✅ Prevent duplicate requests for the same keywords
        String keywordKey = String.join(",", keywords);
        if (activeSearches.putIfAbsent(keywordKey, true) != null) {
            log.warn("⚠️ Duplicate search request detected for: {}", keywords);
         //   return ResponseEntity.status(HttpStatus.CONFLICT)
                //    .body("⚠️ Search already in progress for these keywords. Please wait.");
        }

        try {
            log.info("🚀 Starting search execution...");
            searchService.performSearchesAndProcessLots(keywords);
            log.info("✅ Search execution completed successfully.");
            return ResponseEntity.ok("Search executed successfully.");
        } catch (Exception e) {
            log.error("❌ Search execution failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to execute search: " + e.getMessage());
        } finally {
            activeSearches.remove(keywordKey); // ✅ Remove the keyword after processing
        }
    }
}
