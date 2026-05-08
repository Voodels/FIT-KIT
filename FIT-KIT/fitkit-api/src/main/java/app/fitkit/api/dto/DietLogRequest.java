package app.fitkit.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record DietLogRequest(
        @NotBlank(message = "Journal note cannot be empty")
        @Size(max = 2000, message = "Note is too long")
        String journalNote,
        
        // If the user is logging yesterday's food, they can send the date. Otherwise, we default to today.
        LocalDate loggedDate 
) {}