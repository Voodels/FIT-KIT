package app.fitkit.api.service;

import app.fitkit.api.dto.AuthResponse;
import app.fitkit.api.dto.LoginRequest;
import app.fitkit.api.entity.RefreshToken;
import app.fitkit.api.entity.User;
import app.fitkit.api.security.CustomUserDetails;
import app.fitkit.api.security.JwtProperties;
import app.fitkit.api.security.JwtService;
import app.fitkit.api.repository.RefreshTokenRepository;
import app.fitkit.api.repository.UserRepository;
import app.fitkit.api.util.AuthCookieService;
import app.fitkit.api.util.CookieUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties properties;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthCookieService cookieService;
    private final UserRepository userRepository;

    @Transactional
    public ResponseEntity<AuthResponse> login(LoginRequest request, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.login(), request.password())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        AuthResponse authResponse = issueTokens(user, response);

        return ResponseEntity.ok(authResponse);
    }

    @Transactional
    public ResponseEntity<AuthResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtils.getCookieValue(request, properties.getCookie().getRefreshName());
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token missing");
        }

        Jws<Claims> parsed = jwtService.parseToken(refreshToken);
        Claims claims = parsed.getPayload();
        if (!"refresh".equals(jwtService.getTokenType(claims))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        String tokenId = jwtService.getTokenId(claims);
        UUID userId = jwtService.getUserId(claims);

        RefreshToken storedToken = refreshTokenRepository.findByIdAndRevokedAtIsNull(tokenId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token revoked"));

        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            storedToken.setRevokedAt(LocalDateTime.now());
            refreshTokenRepository.save(storedToken);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }

        String hashed = hashToken(refreshToken);
        if (!hashed.equals(storedToken.getTokenHash()) || !storedToken.getUser().getId().equals(userId)) {
            storedToken.setRevokedAt(LocalDateTime.now());
            refreshTokenRepository.save(storedToken);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token invalid");
        }

        // Rotate refresh token
        storedToken.setRevokedAt(LocalDateTime.now());
        String newTokenId = UUID.randomUUID().toString();
        storedToken.setReplacedByTokenId(newTokenId);
        refreshTokenRepository.save(storedToken);

        AuthResponse authResponse = issueTokens(storedToken.getUser(), response, newTokenId);

        return ResponseEntity.ok(authResponse);
    }

    @Transactional
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtils.getCookieValue(request, properties.getCookie().getRefreshName());
        if (refreshToken != null && !refreshToken.isBlank()) {
            try {
                Jws<Claims> parsed = jwtService.parseToken(refreshToken);
                String tokenId = jwtService.getTokenId(parsed.getPayload());

                refreshTokenRepository.findByIdAndRevokedAtIsNull(tokenId).ifPresent(token -> {
                    token.setRevokedAt(LocalDateTime.now());
                    refreshTokenRepository.save(token);
                });
            } catch (Exception ignored) {
            }
        }

        ResponseCookie clearAccess = cookieService.clearAccessCookie();
        ResponseCookie clearRefresh = cookieService.clearRefreshCookie();
        response.addHeader(HttpHeaders.SET_COOKIE, clearAccess.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, clearRefresh.toString());

        return ResponseEntity.noContent().build();
    }

    private AuthResponse issueTokens(User user, HttpServletResponse response) {
        String refreshTokenId = UUID.randomUUID().toString();
        return issueTokens(user, response, refreshTokenId);
    }

    private AuthResponse issueTokens(User user, HttpServletResponse response, String refreshTokenId) {
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtService.createAccessToken(userDetails);
        String refreshToken = jwtService.createRefreshToken(userDetails, refreshTokenId);

        RefreshToken stored = new RefreshToken();
        stored.setId(refreshTokenId);
        stored.setUser(user);
        stored.setTokenHash(hashToken(refreshToken));
        stored.setCreatedAt(LocalDateTime.now());
        stored.setExpiresAt(LocalDateTime.now().plusDays(properties.getRefreshTokenTtlDays()));
        refreshTokenRepository.save(stored);

        ResponseCookie accessCookie = cookieService.buildAccessCookie(
                accessToken,
                Duration.ofMinutes(properties.getAccessTokenTtlMinutes())
        );
        ResponseCookie refreshCookie = cookieService.buildRefreshCookie(
                refreshToken,
                Duration.ofDays(properties.getRefreshTokenTtlDays())
        );

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        LocalDateTime accessExpiresAt = LocalDateTime.now().plusMinutes(properties.getAccessTokenTtlMinutes());
        return new AuthResponse(
            userDetails.getId(),
            userDetails.getUsername(),
            userDetails.getProfilePicUrl(),
            accessExpiresAt
        );
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to hash refresh token");
        }
    }
}
