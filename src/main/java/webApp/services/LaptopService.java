package webApp.services;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import webApp.models.Laptop;
import webApp.repositories.LaptopRepository;
import webApp.specifications.LaptopSpecifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class LaptopService {
    private static final Logger log = LoggerFactory.getLogger(LaptopService.class);
    @Autowired
    private LaptopRepository laptopRepository;

    // ✅ Fetch laptops with filters
    public List<Laptop> getFilteredLaptops(Map<String, String> filters) {
        Specification<Laptop> spec = LaptopSpecifications.filterByParams(filters);
        return laptopRepository.findAll(spec); // ✅ Apply the filter!
    }

    // ✅ Fetch all laptops (without filters)
    public List<Laptop> getAllLaptops() {
        return laptopRepository.findAll();
    }

    // ✅ Add a new laptop
    public Laptop addLaptop(Laptop laptop) {
        return laptopRepository.save(laptop);
    }

    // ✅ Delete a laptop by ID
    public void deleteLaptop(Long id) {
        laptopRepository.deleteById(id);
    }
    // ✅ Get favorite laptops
    public List<Laptop> getFavoriteLaptops() {
        return laptopRepository.findFavoriteLaptops();
    }


    public boolean toggleFavorite(Long id, boolean isFavorite) {
        Optional<Laptop> optionalLaptop = laptopRepository.findById(id);
        if (optionalLaptop.isPresent()) {
            Laptop laptop = optionalLaptop.get();
            laptop.setFavorite(isFavorite); // Boolean is directly mapped
            laptopRepository.save(laptop);
            log.info("✅ Favorite updated: Laptop ID={} | Favorite={}", id, isFavorite);
            return true;
        }
        log.warn("⚠️ Laptop ID={} not found!", id);
        return false;
    }


    public int clearFavorites() {
        try {
            log.info("🗑️ Attempting to remove all favorites...");
            int deletedCount = laptopRepository.clearAllFavorites(); // Ensure repository method exists

            if (deletedCount == 0) {
                log.warn("⚠️ No favorites found to delete.");
            } else {
                log.info("✅ {} favorite(s) removed.", deletedCount);
            }

            return deletedCount;
        } catch (Exception e) {
            log.error("❌ Error deleting favorites: {}", e.getMessage(), e);
            throw new RuntimeException("Error clearing favorites.", e);
        }
    }


}
