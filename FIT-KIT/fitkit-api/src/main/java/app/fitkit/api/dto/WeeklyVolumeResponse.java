package app.fitkit.api.dto;

import java.time.LocalDateTime;

public record WeeklyVolumeResponse(
        double totalVolumeKg,
        LocalDateTime startDate,
        LocalDateTime endDate
) {}
