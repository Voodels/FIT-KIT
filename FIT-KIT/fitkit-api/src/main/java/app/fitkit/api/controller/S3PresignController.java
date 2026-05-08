package app.fitkit.api.controller;

import app.fitkit.api.dto.PresignDownloadResponse;
import app.fitkit.api.dto.PresignUploadRequest;
import app.fitkit.api.dto.PresignUploadResponse;
import app.fitkit.api.service.S3PresignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/media/presign")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.storage.type", havingValue = "s3")
public class S3PresignController {

    private final S3PresignService presignService;

    @PostMapping("/upload")
    public ResponseEntity<PresignUploadResponse> presignUpload(
            @Valid @RequestBody PresignUploadRequest request) {
        return ResponseEntity.ok(presignService.createPresignedUpload(request));
    }

    @GetMapping("/download")
    public ResponseEntity<PresignDownloadResponse> presignDownload(
            @RequestParam("objectKey") String objectKey) {
        return ResponseEntity.ok(presignService.createPresignedDownload(objectKey));
    }
}
