package webApp.controllers;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import webApp.Models.Laptop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import webApp.services.LaptopService;
import org.slf4j.Logger;
import java.util.List;
import java.util.Map;


@RestController // ✅ API Controller (returns JSON)
@RequestMapping("/api")
@CrossOrigin(origins = "*") // ✅ Allow frontend calls

public class LaptopController {
    private static final Logger log = LoggerFactory.getLogger(LaptopController.class);

    @Autowired
    private LaptopService laptopService;

    // ✅ Fetch all laptops (no filters)
    @GetMapping("/laptops")
    public List<Laptop> getAllLaptops() {
        return laptopService.getAllLaptops(); // Return JSON
    }

    // ✅ Fetch laptops with filters
    @GetMapping("/laptops/filter")
    public List<Laptop> getFilteredLaptops(@RequestParam Map<String, String> filters) {
        return laptopService.getFilteredLaptops(filters);
    }
    // ✅ Fetch all favorite laptops
    @GetMapping("/laptops/favorites")

    public ResponseEntity<List<Laptop>> getFavoriteLaptops() {
        log.info("🔍 Fetching all favorite laptops...");
        List<Laptop> favorites = laptopService.getFavoriteLaptops();
        return ResponseEntity.ok(favorites);
    }

    @PostMapping("/laptops/favorite/{id}")
    public ResponseEntity<String> toggleFavorite(@PathVariable Long id, @RequestBody Map<String, Boolean> requestBody) {
        boolean isFavorite = requestBody.getOrDefault("favorite", false);
        log.info("🔄 Favorite request received: Laptop ID={} | Favorite={}", id, isFavorite);

        boolean updated = laptopService.toggleFavorite(id, isFavorite);
        if (!updated) {
            log.warn("❌ Laptop ID={} not found in database!", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Laptop not found");
        }

        log.info("✅ Favorite status updated successfully for Laptop ID={}", id);
        return ResponseEntity.ok("Favorite status updated");
    }



    @DeleteMapping("/laptops/favorites")
    public ResponseEntity<String> clearFavorites() {
        log.info("🗑️ Deleting ALL favorite laptops...");

        try {
            int deletedCount = laptopService.clearFavorites();

            if (deletedCount == 0) {
                log.warn("⚠️ No favorites found to delete.");
                return ResponseEntity.ok("No favorites found to delete.");
            }

            log.info("✅ {} favorite(s) removed.", deletedCount);
            return ResponseEntity.ok(deletedCount + " favorite(s) removed.");
        } catch (Exception e) {
            log.error("❌ Error deleting favorites:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting favorites.");
        }
    }


}
