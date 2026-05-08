package app.fitkit.api.repository;

import app.fitkit.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    // Spring magically translates this exact method name into:
    // SELECT * FROM users WHERE email = ? AND deleted_at IS NULL;
    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    // SELECT * FROM users WHERE username = ? AND deleted_at IS NULL;
    Optional<User> findByUsernameAndDeletedAtIsNull(String username);

    Optional<User> findByEmailOrUsernameAndDeletedAtIsNull(String email, String username);

    Optional<User> findByIdAndDeletedAtIsNull(UUID id);
    
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}