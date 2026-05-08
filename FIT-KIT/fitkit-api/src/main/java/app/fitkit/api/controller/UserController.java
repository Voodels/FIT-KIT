package app.fitkit.api.controller;

import app.fitkit.api.dto.UserRegistrationRequest;
import app.fitkit.api.dto.UserResponse;
import app.fitkit.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users") // All endpoints in this class will start with /api/users
@RequiredArgsConstructor
public class UserController {

    // Inject the brain (Service layer)
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        
        System.out.println("=== REGISTRATION REQUEST DEBUG ===");
        System.out.println("Username: '" + request.username() + "' (length: " + request.username().length() + ")");
        System.out.println("Email: '" + request.email() + "'");
        System.out.println("Password: '" + request.password() + "' (length: " + request.password().length() + ")");
        System.out.println("================================");
        
        // 1. Hand the validated DTO to the Service layer
        UserResponse response = userService.registerUser(request);
        
        System.out.println("=== REGISTRATION SUCCESS ===");
        System.out.println("Created user ID: " + response.id());
        System.out.println("Created username: " + response.username());
        System.out.println("=============================");
        
        // 2. Return the clean response with a 201 Created status
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}