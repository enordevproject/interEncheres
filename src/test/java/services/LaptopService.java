package services;

import Models.Laptop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repositories.LaptopRepository;
import java.util.List;

@Service
public class LaptopService {

    @Autowired
    private LaptopRepository laptopRepository;

    // ✅ Fetch all laptops
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
