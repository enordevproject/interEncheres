package webApp.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webApp.services.LotService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/lots")
public class LotController {

    private final LotService lotService; // LotService contains your existing processLotsWithGPT()

    public LotController(LotService lotService) {
        this.lotService = lotService;
    }

    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processLots() {
        Map<String, Object> response = new HashMap<>();
        try {
            lotService.processLotsWithGPT();
            response.put("status", "success");
            response.put("message", "Processing complete");
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("status", "error");
            response.put("message", "Error processing lots: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
