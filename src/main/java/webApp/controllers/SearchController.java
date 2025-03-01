package webApp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webApp.services.LotSearchService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
@CrossOrigin(origins = "http://localhost:63342")
@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private LotSearchService searchService;

    @PostMapping("/execute") // ✅ Ensure this is POST
    public CompletableFuture<ResponseEntity<String>> executeSearch(@RequestBody List<String> keywords) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                searchService.performSearchesAndProcessLots(keywords);
                return ResponseEntity.ok("Search executed successfully.");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to execute search: " + e.getMessage());
            }
        });
    }
}
