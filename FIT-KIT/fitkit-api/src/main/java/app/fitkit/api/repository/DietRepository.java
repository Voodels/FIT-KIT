package app.fitkit.api.repository;

import app.fitkit.api.entity.DietLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DietRepository extends JpaRepository<DietLog, UUID> {
    
    // Find a specific day's log for a user
    Optional<DietLog> findByUserIdAndLoggedDate(UUID userId, LocalDate loggedDate);
    
    // Get all logs for a user, sorted by date
    List<DietLog> findByUserIdOrderByLoggedDateDesc(UUID userId);
}
