package pl.sgorski.expense_splitter.features.auth.local.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.exceptions.RefreshTokenValidationException;
import pl.sgorski.expense_splitter.utils.AuthorizationTokenUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public final class RefreshTokenExtractor {

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
            log.debug("Extracted refresh token from cookie");
            return refreshTokenCookie;
        }

        // Try Authorization header (mobile/desktop clients)
        var authHeader = request.getHeader(AuthorizationTokenUtils.AUTHORIZATION_HEADER);
        if (authHeader != null && authHeader.startsWith(AuthorizationTokenUtils.BEARER_PREFIX)) {
            log.debug("Extracted refresh token from Authorization header");
            var tokenString = authHeader.substring(AuthorizationTokenUtils.BEARER_PREFIX.length());
            try {
                return UUID.fromString(tokenString);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid refresh token format in Authorization header: {}", tokenString);
                throw new RefreshTokenValidationException("Invalid refresh token format in Authorization header");
            }
        }

        log.warn("Refresh token not found in cookie or Authorization header");
        throw new RefreshTokenValidationException("Refresh token not found in cookie or Authorization header");
    }
}
