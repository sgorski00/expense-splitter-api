package pl.sgorski.expense_splitter.features.auth.refresh_token.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.exceptions.RefreshTokenValidationException;
import pl.sgorski.expense_splitter.utils.AuthorizationTokenUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public final class RefreshTokenExtractor {

  public UUID extract(@Nullable UUID refreshTokenCookie, HttpServletRequest request)
      throws RefreshTokenValidationException {
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
        throw new RefreshTokenValidationException(
            "Invalid refresh token format in Authorization header");
      }
    }

    log.warn("Refresh token not found in cookie or Authorization header");
    throw new RefreshTokenValidationException(
        "Refresh token not found in cookie or Authorization header");
  }
}
