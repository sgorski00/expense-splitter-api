package pl.sgorski.expense_splitter.utils;

import org.jspecify.annotations.Nullable;

public class AuthorizationTokenUtils {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    public static @Nullable String getTokenFromHeader(@Nullable String header) {
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return null;
        }

        try {
            return header.substring(BEARER_PREFIX.length());
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
}
