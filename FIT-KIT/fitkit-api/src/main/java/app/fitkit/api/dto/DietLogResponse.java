package app.fitkit.api.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record DietLogResponse(
        UUID id,
        String journalNote,
        int totalCalories,
        double totalProtein,
        double totalCarbs,
        double totalFats,
        LocalDate loggedDate,
        List<ParsedItem> extractedItems // The JSON array of food/drinks we found!
) {}