package app.fitkit.api.service;

import app.fitkit.api.dto.PresignDownloadResponse;
import app.fitkit.api.dto.PresignUploadRequest;
import app.fitkit.api.dto.PresignUploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.Duration;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "app.storage.type", havingValue = "s3")
public class S3PresignService {

    private final S3Presigner presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.presign-ttl-minutes:10}")
    private long presignTtlMinutes;

    @Value("${aws.s3.max-upload-bytes:5242880}")
    private long maxUploadBytes;

    @Value("${aws.s3.allowed-content-types:image/jpeg,image/png,image/webp}")
    private String allowedContentTypes;

    public S3PresignService(S3Presigner presigner) {
        this.presigner = presigner;
    }

    public PresignUploadResponse createPresignedUpload(PresignUploadRequest request) {
        validateUpload(request);

        String safeFileName = sanitizeFileName(request.fileName());
        String key = "uploads/" + UUID.randomUUID() + "_" + safeFileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(request.contentType())
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(presignTtlMinutes))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presigned = presigner.presignPutObject(presignRequest);

        String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);
        return new PresignUploadResponse(presigned.url().toString(), key, fileUrl);
    }

    public PresignDownloadResponse createPresignedDownload(String objectKey) {
        String safeKey = sanitizeKey(objectKey);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(safeKey)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(presignTtlMinutes))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presigned = presigner.presignGetObject(presignRequest);
        return new PresignDownloadResponse(presigned.url().toString(), safeKey);
    }

    private String sanitizeFileName(String fileName) {
        String normalized = fileName.replace("\\", "/");
        int lastSlash = normalized.lastIndexOf('/');
        String baseName = lastSlash >= 0 ? normalized.substring(lastSlash + 1) : normalized;
        return baseName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private String sanitizeKey(String key) {
        String normalized = key.replace("\\", "/");
        return normalized.replaceAll("\u0000", "");
    }

    private void validateUpload(PresignUploadRequest request) {
        if (request.fileSize() > maxUploadBytes) {
            throw new ResponseStatusException(
                    HttpStatus.PAYLOAD_TOO_LARGE,
                    "File exceeds max size of " + maxUploadBytes + " bytes"
            );
        }

        Set<String> allowed = Arrays.stream(allowedContentTypes.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .collect(Collectors.toSet());

        if (!allowed.contains(request.contentType())) {
            throw new ResponseStatusException(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Unsupported content type"
            );
        }
    }
}
