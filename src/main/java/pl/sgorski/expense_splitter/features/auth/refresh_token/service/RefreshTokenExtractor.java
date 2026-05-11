package pl.sgorski.expense_splitter.features.auth.refresh_token.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.exceptions.authentication.RefreshTokenValidationException;
import pl.sgorski.expense_splitter.utils.AuthorizationTokenUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public final class RefreshTokenExtractor {

  public Optional<UUID> extract(@Nullable UUID refreshTokenCookie, HttpServletRequest request) {
    return extractFromCookie(refreshTokenCookie).or(() -> extractFromHeader(request));
  }

  public UUID requireExtract(@Nullable UUID refreshTokenCookie, HttpServletRequest request)
      throws RefreshTokenValidationException {
    return extract(refreshTokenCookie, request)
        .orElseThrow(
            () -> new RefreshTokenValidationException("Refresh token not found or invalid"));
  }

  private Optional<UUID> extractFromCookie(@Nullable UUID refreshTokenCookie) {
    return Optional.ofNullable(refreshTokenCookie);
  }

  private static Optional<UUID> extractFromHeader(HttpServletRequest request) {
    var authHeader = request.getHeader(AuthorizationTokenUtils.AUTHORIZATION_HEADER);
    if (authHeader == null || !authHeader.startsWith(AuthorizationTokenUtils.BEARER_PREFIX)) {
      return Optional.empty();
    }
    log.debug("Extracted refresh token from Authorization header");
    var tokenString = authHeader.substring(AuthorizationTokenUtils.BEARER_PREFIX.length());
    try {
      return Optional.of(UUID.fromString(tokenString));
    } catch (IllegalArgumentException e) {
      log.warn("Invalid refresh token format in Authorization header");
      return Optional.empty();
    }
  }
}
