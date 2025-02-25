package controllers;

import Models.Laptop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import services.LaptopService;

import java.util.List;

@RestController
@RequestMapping("/api/laptops")
@CrossOrigin(origins = "*") // Allow frontend requests
public class LaptopController {

    @Autowired
    private LaptopService laptopService;

    // ✅ Fetch all laptops from database
    @GetMapping
    public List<Laptop> getAllLaptops() {
        return laptopService.getAllLaptops();
    }

    // ✅ Add a new laptop
    @PostMapping
    public Laptop addLaptop(@RequestBody Laptop laptop) {
        return laptopService.addLaptop(laptop);
    }

    // ✅ Delete a laptop by ID
    @DeleteMapping("/{id}")
    public String deleteLaptop(@PathVariable Long id) {
        laptopService.deleteLaptop(id);
        return "✅ Laptop deleted successfully!";
    }
}
