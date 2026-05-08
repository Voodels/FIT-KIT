package app.fitkit.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. The Specific Handler (For intentional 404s)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, 
            HttpServletRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(), // 404
                HttpStatus.NOT_FOUND.getReasonPhrase(), // "Not Found"
                ex.getMessage(), // e.g., "Workout not found"
                request.getRequestURI() // e.g., "/api/workouts/99"
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(
            ResponseStatusException ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getStatusCode().value(),
                ex.getStatusCode().toString(),
                ex.getReason(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "Invalid credentials.",
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
                        IllegalArgumentException ex,
                        HttpServletRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                ex.getMessage(),
                                request.getRequestURI()
                );

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        System.out.println("=== VALIDATION ERROR DEBUG ===");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Validation errors:");
        
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    System.out.println("- Field: " + error.getField() + ", Rejected value: '" + error.getRejectedValue() + "', Message: " + error.getDefaultMessage());
                    return error.getField() + ": " + error.getDefaultMessage();
                })
                .collect(Collectors.joining(", "));
        
        System.out.println("=============================");

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                errorMessage,
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex,
            HttpServletRequest request) {
        
        System.out.println("=== CONSTRAINT VIOLATION DEBUG ===");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Constraint violations:");
        
        String errorMessage = ex.getConstraintViolations()
                .stream()
                .map(violation -> {
                    System.out.println("- Property: " + violation.getPropertyPath() + ", Rejected value: '" + violation.getInvalidValue() + "', Message: " + violation.getMessage());
                    return violation.getPropertyPath().toString() + ": " + violation.getMessage();
                })
                .collect(Collectors.joining(", "));
        
        System.out.println("=================================");

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                errorMessage,
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 2. The Generic Fallback (For unexpected server crashes)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, 
            HttpServletRequest request) {
        
        // In a real SDE environment, you would log 'ex.getMessage()' to the console here so you can debug it later.
        System.err.println("CRITICAL ERROR CAUGHT: " + ex.getMessage());
        
        // Check if this is a validation error that should be handled as 400
        String errorMessage = ex.getMessage();
        System.out.println("=== DEBUG: Checking error message ===");
        System.out.println("Error message: " + errorMessage);
        System.out.println("Contains 'Validation failed for argument': " + (errorMessage != null && errorMessage.contains("Validation failed for argument")));
        System.out.println("=====================================");
        
        if (errorMessage != null && errorMessage.contains("Validation failed for argument")) {
            System.out.println("=== VALIDATION ERROR DETECTED IN GENERIC HANDLER ===");
            System.out.println("Error message: " + errorMessage);
            System.out.println("Request URI: " + request.getRequestURI());
            
            // Extract validation error details from the message
            String validationMessage = "Validation failed. Please check your input.";
            if (errorMessage.contains("Username must be between 3 and 20 characters")) {
                validationMessage = "Username must be between 3 and 20 characters";
            } else if (errorMessage.contains("Password must be at least 8 characters")) {
                validationMessage = "Password must be at least 8 characters";
            } else if (errorMessage.contains("Invalid email format")) {
                validationMessage = "Invalid email format";
            }
            
            // If both username and password errors exist, show both
            if (errorMessage.contains("Username must be between 3 and 20 characters") && 
                errorMessage.contains("Password must be at least 8 characters")) {
                validationMessage = "Username must be between 3 and 20 characters and password must be at least 8 characters";
            }
            
            System.out.println("Returning validation error: " + validationMessage);
            System.out.println("===================================================");
            
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(), // 400
                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    validationMessage,
                    request.getRequestURI()
            );
            
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), // 500
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), // "Internal Server Error"
                "An unexpected error occurred on our end. Please try again.", // Hide the real Java error from the user
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}