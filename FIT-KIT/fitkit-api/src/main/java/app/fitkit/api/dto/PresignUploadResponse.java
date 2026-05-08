package app.fitkit.api.dto;

public record PresignUploadResponse(
        String uploadUrl,
        String objectKey,
        String fileUrl
) {
}
