package pl.sgorski.expense_splitter.features.auth.local.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.exceptions.RefreshTokenValidationException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public final class RefreshTokenExtractor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Extracts refresh token from either httpOnly cookie or Authorization header.
     * <br>
     * Web clients send token in cookie, mobile/desktop clients send in Authorization header.
     *
     * @param refreshTokenCookie refresh token from cookie (nullable)
     * @param request HTTP request
     * @return UUID of refresh token
     * @throws IllegalArgumentException if token not found in either source
     */
    public UUID extract(@Nullable UUID refreshTokenCookie, HttpServletRequest request) {
        // Try cookie (web clients)
        if (refreshTokenCookie != null) {
            return refreshTokenCookie;
        }

        // Try Authorization header (mobile/desktop clients)
        var authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            var tokenString = authHeader.substring(BEARER_PREFIX.length());
            try {
                return UUID.fromString(tokenString);
            } catch (IllegalArgumentException e) {
                throw new RefreshTokenValidationException("Invalid refresh token format in Authorization header");
            }
        }

        throw new RefreshTokenValidationException("Refresh token not found in cookie or Authorization header");
    }
}
