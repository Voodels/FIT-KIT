package app.fitkit.api.repository;

import app.fitkit.api.entity.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, UUID> {

    // THE SDE MATH ENGINE
    // We write a custom JPQL (Java Persistence Query Language) statement.
    // It automatically multiplies weight * reps for every set, and then SUMs them all together.
    @Query("""
           SELECT COALESCE(SUM(s.weight * s.reps), 0) 
           FROM WorkoutSet s 
           JOIN s.workoutLog w 
           WHERE w.user.id = :userId 
           AND w.loggedAt >= :startDate 
           AND w.loggedAt <= :endDate
           """)
    double calculateTotalVolumeForUserAndDateRange(
            @Param("userId") UUID userId, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate
    );
}