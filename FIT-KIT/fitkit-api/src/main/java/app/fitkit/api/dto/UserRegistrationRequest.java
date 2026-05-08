package app.fitkit.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// we use 'record' instead of 'class'. 
// Java 21 automatically generates getters, a constructor, equals(), and hashCode() for us!
public record UserRegistrationRequest(
        
        @NotBlank(message = "Username cannot be blank")
        @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
        String username,
        
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email format")
        String email,
        
        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password
) {}