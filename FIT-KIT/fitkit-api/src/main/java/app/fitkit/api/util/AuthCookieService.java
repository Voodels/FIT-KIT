package app.fitkit.api.util;

import app.fitkit.api.security.JwtProperties;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class AuthCookieService {

    private final JwtProperties properties;

    public AuthCookieService(JwtProperties properties) {
        this.properties = properties;
    }

    public ResponseCookie buildAccessCookie(String token, Duration maxAge) {
        return buildCookie(properties.getCookie().getAccessName(), token, maxAge);
    }

    public ResponseCookie buildRefreshCookie(String token, Duration maxAge) {
        return buildCookie(properties.getCookie().getRefreshName(), token, maxAge);
    }

    public ResponseCookie clearAccessCookie() {
        return buildCookie(properties.getCookie().getAccessName(), "", Duration.ZERO);
    }

    public ResponseCookie clearRefreshCookie() {
        return buildCookie(properties.getCookie().getRefreshName(), "", Duration.ZERO);
    }

    private ResponseCookie buildCookie(String name, String value, Duration maxAge) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(properties.getCookie().isSecure())
                .sameSite(properties.getCookie().getSameSite())
                .path(properties.getCookie().getPath())
                .maxAge(maxAge);

        String domain = properties.getCookie().getDomain();
        if (domain != null && !domain.isBlank()) {
            builder.domain(domain);
        }

        return builder.build();
    }
}
