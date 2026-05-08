package app.fitkit.api.controller;

import app.fitkit.api.dto.WeeklyVolumeResponse;
import app.fitkit.api.security.CustomUserDetails;
import app.fitkit.api.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/weekly-volume")
    public ResponseEntity<WeeklyVolumeResponse> getWeeklyVolume(
            @AuthenticationPrincipal CustomUserDetails user) {
        WeeklyVolumeResponse response = analyticsService.calculateWeeklyVolume(user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/distribution")
    public ResponseEntity<Map<String, Double>> getMuscleDistribution(
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(analyticsService.getMuscleDistribution(user.getId()));
    }
}
