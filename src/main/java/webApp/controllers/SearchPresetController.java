package webApp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webApp.models.SearchPreset;
import webApp.services.SearchPresetService;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/search-presets")
public class SearchPresetController {

    @Autowired
    private SearchPresetService service;

    // ✅ Save a new preset
    @PostMapping("/save")
    public ResponseEntity<SearchPreset> savePreset(@RequestBody Map<String, Object> payload) {
        String name = (String) payload.get("presetName");
        Map<String, String> filters = (Map<String, String>) payload.get("filters"); // Stores filters

        SearchPreset preset = new SearchPreset(name, filters);
        return ResponseEntity.ok(service.savePreset(preset));
    }

    // ✅ Load an existing preset
    @GetMapping("/load/{name}")
    public ResponseEntity<SearchPreset> loadPreset(@PathVariable String name) {
        Optional<SearchPreset> preset = service.getPreset(name);
        return preset.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Get all saved presets
    @GetMapping("/all")
    public ResponseEntity<List<SearchPreset>> getAllPresets() {
        return ResponseEntity.ok(service.getAllPresets());
    }

    @DeleteMapping("/delete/{name}")
    public ResponseEntity<?> deletePreset(@PathVariable String name) {
        try {
            if (!service.presetExists(name)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "❌ Preset not found"));
            }

            service.deletePreset(name);
            return ResponseEntity.ok(Map.of("message", "✅ Preset deleted successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "❌ Failed to delete preset", "details", e.getMessage()));
        }
    }



}
