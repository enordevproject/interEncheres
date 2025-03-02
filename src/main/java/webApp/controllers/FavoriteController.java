package webApp.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webApp.services.FavoriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@CrossOrigin(origins = "*") // ✅ Allow frontend calls
@RequestMapping("/api/interencheres")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private static final Logger log = LoggerFactory.getLogger(FavoriteController.class);

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @CrossOrigin(origins = "*") // ✅ Allow frontend calls
    @PostMapping("/favorite")
    public ResponseEntity<String> addToFavorites(@RequestBody FavoriteRequest request) {
        log.info("🔄 Adding lot to favorites: {}", request.getLotUrl());
        String result = favoriteService.addLotToFavorites(request.getLotUrl());
        return ResponseEntity.ok(result);
    }

    static class FavoriteRequest {
        private String lotUrl;
        public String getLotUrl() { return lotUrl; }
    }
}
