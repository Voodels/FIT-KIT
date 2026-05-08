package app.fitkit.api.controller;

import app.fitkit.api.dto.DietLogRequest;
import app.fitkit.api.dto.DietLogResponse;
import app.fitkit.api.security.CustomUserDetails;
import app.fitkit.api.service.DietService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diet")
@RequiredArgsConstructor
public class DietController {

    private final DietService dietService;

    // We use a POST request here, but remember our Service acts as an "Upsert" (Update or Insert)
    @PostMapping("/card")
    public ResponseEntity<DietLogResponse> saveDailyCard(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody DietLogRequest request) {

        DietLogResponse response = dietService.saveOrUpdateDailyCard(user.getId(), request);
        
        // We return 200 OK instead of 201 Created because this endpoint might just be updating an existing card
        return ResponseEntity.ok(response);
    }
}