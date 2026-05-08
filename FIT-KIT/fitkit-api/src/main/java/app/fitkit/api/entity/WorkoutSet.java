package app.fitkit.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "workout_sets")
@Getter
@Setter
@NoArgsConstructor
public class WorkoutSet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    // --- THE RELATIONSHIP ---
    // Many Sets belong to One Workout Log.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_log_id", nullable = false)
    private WorkoutLog workoutLog;

    @Column(nullable = false, length = 100)
    private String exerciseName; // e.g., "Barbell Bench Press"

    @Column(nullable = false)
    private int setNumber; // e.g., Set 1, Set 2

    @Column(nullable = false)
    private double weight; // Stored in kg or lbs based on user preference

    @Column(nullable = false)
    private int reps;
}