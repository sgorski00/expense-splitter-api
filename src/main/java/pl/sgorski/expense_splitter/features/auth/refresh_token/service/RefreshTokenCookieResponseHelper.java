package pl.sgorski.expense_splitter.features.auth.refresh_token.service;

import java.util.UUID;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public final class RefreshTokenCookieResponseHelper {

  public static final String REFRESH_TOKEN_COOKIE_KEY = "refreshToken";
  private static final String COOKIE_PATH = "/api";
  private static final String SAME_SITE = "Strict";
  private static final boolean HTTP_ONLY = true;
  private static final boolean SECURE = true;

  public ResponseCookie createRefreshTokenCookie(UUID tokenValue, long maxAgeSec) {
    return ResponseCookie.from(REFRESH_TOKEN_COOKIE_KEY, tokenValue.toString())
        .httpOnly(HTTP_ONLY)
        .secure(SECURE)
        .path(COOKIE_PATH)
        .maxAge(maxAgeSec)
        .sameSite(SAME_SITE)
        .build();
  }

  /** Creates an empty refresh token cookie that overrides the present one. */
  public ResponseCookie createClearRefreshTokenCookie() {
    return ResponseCookie.from(REFRESH_TOKEN_COOKIE_KEY, "").path(COOKIE_PATH).maxAge(0).build();
  }
}
