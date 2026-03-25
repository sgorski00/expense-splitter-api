package pl.sgorski.expense_splitter.security.oauth2;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

/**
 * Helper class for managing HTTP response cookies (JWT access tokens).
 * <br>
 * Provides secure httpOnly cookie configuration for short-lived JWT tokens.
 */
@Component
public final class AccessTokenCookieResponseHelper {

    public static final String ACCESS_TOKEN_COOKIE_KEY = "accessToken";
    private static final String COOKIE_PATH = "/api";
    private static final String SAME_SITE = "Strict";
    private static final boolean HTTP_ONLY = true;
    private static final boolean SECURE = true;

    /**
     * Creates an access token cookie with secure settings.
     * <br>
     * Used during OAuth2 authentication to store JWT token securely.
     *
     * @param tokenValue the JWT token value
     * @param maxAgeSec  maximum age in seconds
     * @return configured ResponseCookie
     */
    public ResponseCookie createAccessTokenCookie(String tokenValue, long maxAgeSec) {
        return ResponseCookie.from(ACCESS_TOKEN_COOKIE_KEY, tokenValue)
                .httpOnly(HTTP_ONLY)
                .secure(SECURE)
                .path(COOKIE_PATH)
                .maxAge(maxAgeSec)
                .sameSite(SAME_SITE)
                .build();
    }

    /**
     * Creates an empty access token cookie (for logout).
     *
     * @return configured ResponseCookie for clearing the cookie
     */
    public ResponseCookie createClearAccessTokenCookie() {
        return ResponseCookie.from(ACCESS_TOKEN_COOKIE_KEY, "")
                .path(COOKIE_PATH)
                .maxAge(0)
                .build();
    }
}

