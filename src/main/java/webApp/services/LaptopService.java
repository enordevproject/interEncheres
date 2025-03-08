package webApp.services;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import webApp.models.Laptop;
import webApp.models.Results;
import webApp.repositories.LaptopRepository;
import webApp.specifications.LaptopSpecifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    /**
     * ✅ Deletes expired laptops (date older than yesterday)
     */
    @Transactional
    public void deleteExpiredLaptops() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Fetch all laptops because dates are stored as strings
        List<Laptop> allLaptops = laptopRepository.findAll();

        // Filter expired laptops by converting their String date into LocalDate
        List<Laptop> expiredLaptops = allLaptops.stream()
                .filter(laptop -> {
                    try {
                        LocalDate laptopDate = LocalDate.parse(laptop.getDate(), formatter);
                        return laptopDate.isBefore(yesterday); // Remove if older than yesterday
                    } catch (Exception e) {
                        System.out.println("⚠️ Skipping invalid date: " + laptop.getDate());
                        return false; // Skip if parsing fails
                    }
                })
                .collect(Collectors.toList());

        // Delete expired laptops
        if (!expiredLaptops.isEmpty()) {
            laptopRepository.deleteAll(expiredLaptops);
            System.out.println("🗑 Deleted " + expiredLaptops.size() + " expired laptops.");
        } else {
            System.out.println("✅ No expired laptops found.");
        }
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
