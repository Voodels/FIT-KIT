package app.fitkit.api.dto;

// This acts as both the DTO and the JSON structure stored in the database
public record ParsedItem(
        String name,
        String category, // "FOOD" or "DRINK"
        int calories,
        double protein,
        double carbs,
        double fats
) {}