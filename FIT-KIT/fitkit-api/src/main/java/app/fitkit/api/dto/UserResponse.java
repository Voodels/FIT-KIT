package app.fitkit.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String profilePicUrl,
        LocalDateTime joinedAt
) {}    