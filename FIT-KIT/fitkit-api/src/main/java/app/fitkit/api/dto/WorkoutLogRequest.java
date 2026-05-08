package app.fitkit.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record WorkoutLogRequest(

        @NotBlank(message = "Photo URL is required")
        String photoUrl,

        @Size(max = 500, message = "Caption cannot exceed 500 characters")
        String caption,

        @NotEmpty(message = "At least one muscle group must be targeted")
        List<String> musclesTargeted,

        @Valid
        List<WorkoutSetRequest> sets
) {}