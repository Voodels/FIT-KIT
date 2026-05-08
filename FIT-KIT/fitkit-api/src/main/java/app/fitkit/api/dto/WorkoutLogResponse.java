package app.fitkit.api.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record WorkoutLogResponse(
        UUID id,
        String photoUrl,
        String caption,
        List<String> musclesTargeted,
        LocalDateTime loggedAt,
        
        // --- The "Hydrated" User Data ---
        // We flatten the user data here so the Next.js frontend doesn't have to make a second API call
        UUID authorId,
        String authorUsername,
        String authorProfilePicUrl
) {}