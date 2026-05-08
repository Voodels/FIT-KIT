package app.fitkit.api.service;

import app.fitkit.api.dto.UserRegistrationRequest;
import app.fitkit.api.dto.UserResponse;
import app.fitkit.api.entity.Role;
import app.fitkit.api.entity.User;
import app.fitkit.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor // Lombok generates a constructor for our injected dependencies
public class UserService {

    // We inject the repository so the service can talk to the database
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse registerUser(UserRegistrationRequest request) {
        
        // --- DEV BYPASS: If user exists, just return them (acts as a dev login) ---
        var existingUser = userRepository.findByEmailAndDeletedAtIsNull(request.email());
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getProfilePicUrl(),
                user.getCreatedAt()
            );
        }

        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        // 2. Map DTO to Entity
        User newUser = new User();
        newUser.setUsername(request.username());
        newUser.setEmail(request.email());
        
        newUser.setPasswordHash(passwordEncoder.encode(request.password()));
        newUser.setRoles(Set.of(Role.USER));

        // 3. Save to Neon DB
        User savedUser = userRepository.saveAndFlush(newUser);

        // 4. Map Entity back to safe DTO to return to Next.js
        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getProfilePicUrl(),
                savedUser.getCreatedAt()
        );
    }
}