package webApp.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import webApp.Models.Laptop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LaptopRepository extends JpaRepository<Laptop, Long>, JpaSpecificationExecutor<Laptop> {

    // ✅ Fetch all favorite laptops
    List<Laptop> findByFavorite(Boolean favorite);
    // ✅ Corrected Query for Finding Favorite Laptops
    @Query("SELECT l FROM Laptop l WHERE l.favorite = true")
    List<Laptop> findFavoriteLaptops();

    // ✅ Remove all favorites
    @Modifying
    @Transactional
    @Query("UPDATE Laptop l SET l.favorite = NULL WHERE l.favorite = TRUE")
    int clearAllFavorites();






}
