package app.fitkit.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record PresignUploadRequest(
        @NotBlank(message = "File name cannot be blank")
        String fileName,
        @NotBlank(message = "Content type cannot be blank")
        String contentType,
        @Positive(message = "File size must be greater than 0")
        long fileSize
) {
}
