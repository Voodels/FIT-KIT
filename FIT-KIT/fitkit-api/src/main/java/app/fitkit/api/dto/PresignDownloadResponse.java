package app.fitkit.api.dto;

public record PresignDownloadResponse(
        String downloadUrl,
        String objectKey
) {
}
