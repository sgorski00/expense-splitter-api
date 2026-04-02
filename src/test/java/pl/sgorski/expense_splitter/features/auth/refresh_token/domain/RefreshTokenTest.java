package pl.sgorski.expense_splitter.features.auth.refresh_token.domain;

import org.junit.jupiter.api.Test;
import pl.sgorski.expense_splitter.exceptions.RefreshTokenValidationException;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RefreshTokenTest {

    @Test
    void validate_shouldNotThrow_whenTokenIsValid() {
        var token = new RefreshToken();
        token.setRevoked(false);
        token.setExpiresAt(Instant.now().plusSeconds(60));

        assertDoesNotThrow(token::validate);
    }

    @Test
    void validate_shouldThrow_whenTokenIsRevoked() {
        var token = new RefreshToken();
        token.setRevoked(true);
        token.setExpiresAt(Instant.now().plusSeconds(60));

        assertThrows(RefreshTokenValidationException.class, token::validate);
    }

    @Test
    void validate_shouldThrow_whenTokenIsExpired() {
        var token = new RefreshToken();
        token.setRevoked(false);
        token.setExpiresAt(Instant.now().minusSeconds(60));

        assertThrows(RefreshTokenValidationException.class, token::validate);
    }
}
