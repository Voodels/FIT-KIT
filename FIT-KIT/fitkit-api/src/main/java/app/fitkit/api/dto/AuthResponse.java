package app.fitkit.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuthResponse(
        UUID userId,
        String username,
        String profilePicUrl,
        LocalDateTime accessTokenExpiresAt
) {
}
