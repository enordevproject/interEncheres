package webApp.controllers;

import webApp.Models.Laptop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import webApp.services.LaptopService;

import java.util.List;
import java.util.Map;

@RestController // ✅ API Controller (returns JSON)
@RequestMapping("/api")
@CrossOrigin(origins = "*") // ✅ Allow frontend calls
public class LaptopController {

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
}
