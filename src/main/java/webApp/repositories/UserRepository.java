package webApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webApp.models.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * ✅ Fetches the first user from the database.
     */
    Optional<User> findFirstByOrderByIdAsc();

    /**
     * ✅ Finds a user by email.
     */
    Optional<User> findByEmail(String email);
}
