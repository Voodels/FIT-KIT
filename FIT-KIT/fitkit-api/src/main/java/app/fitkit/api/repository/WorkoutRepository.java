package app.fitkit.api.repository;

import app.fitkit.api.entity.WorkoutLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WorkoutRepository extends JpaRepository<WorkoutLog, UUID> {

    // Spring translates this to: 
    // SELECT * FROM workout_logs WHERE user_id = ? ORDER BY logged_at DESC LIMIT ? OFFSET ?
    Page<WorkoutLog> findByUserIdOrderByLoggedAtDesc(UUID userId, Pageable pageable);

}