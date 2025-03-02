package webApp.repositories;



import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import webApp.models.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LotRepository extends JpaRepository<Lot, Long>, JpaSpecificationExecutor<Lot> {

    /**
     * ✅ Fetch all lots
     */
    List<Lot> findAll();

    /**
     * ✅ Delete all lots from the database
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Lot")
    void deleteAllLots();

    /**
     * ✅ Count the number of lots in the database
     */
    @Query("SELECT COUNT(l) FROM Lot l")
    long countAllLots();
}
