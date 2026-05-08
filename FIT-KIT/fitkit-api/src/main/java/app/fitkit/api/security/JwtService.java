package app.fitkit.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final JwtProperties properties;
    private final SecretKey secretKey;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        byte[] keyBytes = Decoders.BASE64.decode(properties.getSecret());
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(CustomUserDetails user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(properties.getAccessTokenTtlMinutes(), ChronoUnit.MINUTES);

        Set<String> roles = user.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toSet());

        return Jwts.builder()
                .subject(user.getId().toString())
                .issuer(properties.getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .claim("type", "access")
                .claim("roles", roles)
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(CustomUserDetails user, String tokenId) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(properties.getRefreshTokenTtlDays(), ChronoUnit.DAYS);

        return Jwts.builder()
                .subject(user.getId().toString())
                .issuer(properties.getIssuer())
                .id(tokenId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .claim("type", "refresh")
                .signWith(secretKey)
                .compact();
    }

    public Jws<Claims> parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .requireIssuer(properties.getIssuer())
                .build()
                .parseSignedClaims(token);
    }

    public UUID getUserId(Claims claims) {
        return UUID.fromString(claims.getSubject());
    }

    public String getTokenType(Claims claims) {
        Object type = claims.get("type");
        return type != null ? type.toString() : "";
    }

    public String getTokenId(Claims claims) {
        return claims.getId();
    }
}
