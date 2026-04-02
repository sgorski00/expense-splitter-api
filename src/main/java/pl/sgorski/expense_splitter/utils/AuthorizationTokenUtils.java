package pl.sgorski.expense_splitter.utils;

import org.jspecify.annotations.Nullable;

public final class AuthorizationTokenUtils {

  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String BEARER_PREFIX = "Bearer ";

  public static @Nullable String getTokenFromHeader(@Nullable String header) {
    if (header == null || !header.startsWith(BEARER_PREFIX)) {
      return null;
    }

    try {
      var token = header.substring(BEARER_PREFIX.length());
      if (token.isBlank()) {
        return null;
      }
      return token;
    } catch (IndexOutOfBoundsException e) {
      return null;
    }
  }
}
