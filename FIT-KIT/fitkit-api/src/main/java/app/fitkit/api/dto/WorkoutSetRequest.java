package app.fitkit.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record WorkoutSetRequest(
    @NotBlank(message = "Exercise name is required")
    String exerciseName,

    @Positive(message = "Weight must be positive")
    double weight,

    @Positive(message = "Reps must be positive")
    int reps
) {}
