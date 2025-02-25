package webApp.services;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import webApp.Models.Laptop;
import webApp.repositories.LaptopRepository;
import webApp.specifications.LaptopSpecifications;

import java.util.List;
import java.util.Map;

@Service
public class LaptopService {

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
}
