package app.fitkit.api.service;

import app.fitkit.api.dto.WorkoutLogRequest;
import app.fitkit.api.dto.WorkoutLogResponse;
import app.fitkit.api.entity.User;
import app.fitkit.api.entity.WorkoutLog;
import app.fitkit.api.exception.ResourceNotFoundException;
import app.fitkit.api.repository.UserRepository;
import app.fitkit.api.repository.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    // Injecting both repositories because the Workout needs to be linked to a User
    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;

    // 1. Create a new workout
    @Transactional
    public WorkoutLogResponse createWorkout(UUID userId, WorkoutLogRequest request) {
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        WorkoutLog workout = new WorkoutLog();
        workout.setUser(user);
        workout.setPhotoUrl(request.photoUrl());
        workout.setCaption(request.caption());
        workout.setMusclesTargeted(request.musclesTargeted()); 

        // Handle sets if provided
        if (request.sets() != null) {
            for (int i = 0; i < request.sets().size(); i++) {
                var setReq = request.sets().get(i);
                app.fitkit.api.entity.WorkoutSet set = new app.fitkit.api.entity.WorkoutSet();
                set.setWorkoutLog(workout);
                set.setExerciseName(setReq.exerciseName());
                set.setWeight(setReq.weight());
                set.setReps(setReq.reps());
                set.setSetNumber(i + 1);
                workout.getSets().add(set);
            }
        }

        WorkoutLog savedWorkout = workoutRepository.save(workout);

        return mapToResponse(savedWorkout);
    }

    // 2. Get the paginated feed
    @Transactional(readOnly = true) // readOnly = true makes PostgreSQL queries slightly faster
    public Page<WorkoutLogResponse> getUserWorkouts(UUID userId, int page, int size) {
        
        // Spring's built-in pagination object
        PageRequest pageRequest = PageRequest.of(page, size);
        
        // Fetch the raw entities from Neon DB
        Page<WorkoutLog> workoutPage = workoutRepository.findByUserIdOrderByLoggedAtDesc(userId, pageRequest);

        // The .map() function seamlessly loops through the page and converts Entities to DTOs
        return workoutPage.map(this::mapToResponse);
    }

    // --- Private Helper Method to keep code DRY (Don't Repeat Yourself) ---
    private WorkoutLogResponse mapToResponse(WorkoutLog workout) {
        return new WorkoutLogResponse(
                workout.getId(),
                workout.getPhotoUrl(),
                workout.getCaption(),
                workout.getMusclesTargeted(),
                workout.getLoggedAt(),
                
                // Hydrating the User data for the frontend
                workout.getUser().getId(),
                workout.getUser().getUsername(),
                workout.getUser().getProfilePicUrl()
        );
    }
}