package pl.sgorski.expense_splitter.features.auth.refresh_token.service;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RefreshTokenCookieResponseHelperTest {

    private final RefreshTokenCookieResponseHelper helper = new RefreshTokenCookieResponseHelper();

    @Test
    void createRefreshTokenCookie_shouldSetAllPropertiesCorrectly() {
        var token = UUID.randomUUID();
        var maxAge = 3600;

        var cookie = helper.createRefreshTokenCookie(token, maxAge);

        assertEquals(RefreshTokenCookieResponseHelper.REFRESH_TOKEN_COOKIE_KEY, cookie.getName());
        assertEquals(token.toString(), cookie.getValue());
        assertEquals("/api", cookie.getPath());
        assertEquals(Duration.ofSeconds(maxAge), cookie.getMaxAge());
        assertTrue(cookie.isHttpOnly());
        assertTrue(cookie.isSecure());
        assertEquals("Strict", cookie.getSameSite());
    }

    @Test
    void createClearRefreshTokenCookie_shouldHaveEmptyValueAndZeroMaxAge() {
        var cookie = helper.createClearRefreshTokenCookie();

        assertEquals(RefreshTokenCookieResponseHelper.REFRESH_TOKEN_COOKIE_KEY, cookie.getName());
        assertEquals("", cookie.getValue());
        assertEquals("/api", cookie.getPath());
        assertEquals(Duration.ZERO, cookie.getMaxAge());
    }
}
