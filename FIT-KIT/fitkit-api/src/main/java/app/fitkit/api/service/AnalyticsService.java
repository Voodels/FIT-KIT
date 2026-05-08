package app.fitkit.api.service;

import app.fitkit.api.dto.WeeklyVolumeResponse;
import app.fitkit.api.repository.WorkoutRepository;
import app.fitkit.api.repository.WorkoutSetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final WorkoutSetRepository workoutSetRepository;
    private final WorkoutRepository workoutRepository;

    @Transactional(readOnly = true)
    public WeeklyVolumeResponse calculateWeeklyVolume(UUID userId) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(7);

        double totalVolume = workoutSetRepository.calculateTotalVolumeForUserAndDateRange(
                userId, startDate, endDate
        );

        return new WeeklyVolumeResponse(totalVolume, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public Map<String, Double> getMuscleDistribution(UUID userId) {
        // Fetch all workouts for user
        var workouts = workoutRepository.findByUserIdOrderByLoggedAtDesc(userId, org.springframework.data.domain.Pageable.unpaged());
        
        Map<String, Integer> counts = new HashMap<>();
        workouts.forEach(w -> {
            w.getMusclesTargeted().forEach(m -> {
                counts.put(m, counts.getOrDefault(m, 0) + 1);
            });
        });

        // Normalize to 0-100 for the frontend heatmap
        if (counts.isEmpty()) return new HashMap<>();
        
        int max = counts.values().stream().max(Integer::compare).get();
        Map<String, Double> distribution = new HashMap<>();
        counts.forEach((muscle, count) -> {
            distribution.put(muscle, (count * 100.0) / max);
        });

        return distribution;
    }
}
