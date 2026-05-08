package app.fitkit.api.controller;

import app.fitkit.api.dto.WorkoutLogRequest;
import app.fitkit.api.dto.WorkoutLogResponse;
import app.fitkit.api.security.CustomUserDetails;
import app.fitkit.api.service.WorkoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workouts") // All endpoints start with /api/workouts
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;

    // 1. Post a new workout
    @PostMapping
    public ResponseEntity<WorkoutLogResponse> createWorkout(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody WorkoutLogRequest request) {

        WorkoutLogResponse response = workoutService.createWorkout(user.getId(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED); // 201 Created
    }

    // 2. Get the workout feed
    @GetMapping
    public ResponseEntity<Page<WorkoutLogResponse>> getUserWorkouts(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(defaultValue = "0") int page, // Defaults to first page if Next.js forgets to send it
            @RequestParam(defaultValue = "10") int size) { // Defaults to 10 posts per load

        Page<WorkoutLogResponse> response = workoutService.getUserWorkouts(user.getId(), page, size);
        return ResponseEntity.ok(response); // 200 OK
    }
}