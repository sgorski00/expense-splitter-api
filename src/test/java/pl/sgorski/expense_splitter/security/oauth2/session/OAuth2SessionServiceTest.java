package pl.sgorski.expense_splitter.security.oauth2.session;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class OAuth2SessionServiceTest {

    private HttpSession session;
    private OAuth2SessionService service;

    @BeforeEach
    void setUp() {
        session = mock(HttpSession.class);
        service = new OAuth2SessionService();
    }

    @Test
    void isLinkMode_shouldReturnTrue_whenLinkModeLowerCase() {
        when(session.getAttribute(anyString())).thenReturn("link");

        var result = service.isLinkMode(session);

        assertTrue(result);
    }

    @Test
    void isLinkMode_shouldReturnTrue_whenLinkModeUpperCase() {
        when(session.getAttribute(anyString())).thenReturn("LINK");

        var result = service.isLinkMode(session);

        assertTrue(result);
    }

    @Test
    void isLinkMode_shouldReturnTrue_whenLinkModeMixesCase() {
        when(session.getAttribute(anyString())).thenReturn("LiNk");

        var result = service.isLinkMode(session);

        assertTrue(result);
    }

    @Test
    void isLinkMode_shouldReturnFalse_whenNotLinkMode() {
        when(session.getAttribute(anyString())).thenReturn("common");

        var result = service.isLinkMode(session);

        assertFalse(result);
    }

    @Test
    void isLinkMode_shouldReturnFalse_whenNull() {
        when(session.getAttribute(anyString())).thenReturn(null);

        var result = service.isLinkMode(session);

        assertFalse(result);
    }

    @Test
    void isLinkMode_shouldReturnFalse_whenBlank() {
        when(session.getAttribute(anyString())).thenReturn("    ");

        var result = service.isLinkMode(session);

        assertFalse(result);
    }

    @Test
    void getOAuthLinkUserId_shouldReturnUserId() {
        var userId = UUID.randomUUID();
        when(session.getAttribute(anyString())).thenReturn(userId);

        var result = service.getOAuthLinkUserId(session);

        assertEquals(userId, result);
    }

    @Test
    void getOAuthLinkUserId_shouldReturnNull_whenAttributeNotPresent() {
        when(session.getAttribute(anyString())).thenReturn(null);

        var result = service.getOAuthLinkUserId(session);

        assertNull(result);
    }

    @Test
    void clearOAuthAttributes_shouldRemoveAttributes() {
        service.clearOAuthAttributes(session);

        verify(session, atLeast(2)).removeAttribute(anyString());
    }
}
