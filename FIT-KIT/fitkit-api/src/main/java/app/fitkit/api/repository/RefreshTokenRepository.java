package app.fitkit.api.repository;

import app.fitkit.api.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByIdAndRevokedAtIsNull(String id);
    List<RefreshToken> findAllByUserIdAndRevokedAtIsNull(UUID userId);
    long deleteByExpiresAtBefore(LocalDateTime cutoff);
}
