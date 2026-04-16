package pl.sgorski.expense_splitter.features.auth.password_reset_token.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import pl.sgorski.expense_splitter.exceptions.authentication.PasswordResetTokenValidationException;

public class PasswordResetTokenTest {

  @Test
  void validate_shouldNotThrow_whenTokenIsValid() {
    var token = new PasswordResetToken();
    token.setRevoked(false);
    token.setExpiresAt(Instant.now().plusSeconds(60));

    assertDoesNotThrow(token::validate);
  }

  @Test
  void validate_shouldThrow_whenTokenIsRevoked() {
    var token = new PasswordResetToken();
    token.setRevoked(true);
    token.setExpiresAt(Instant.now().plusSeconds(60));

    assertThrows(PasswordResetTokenValidationException.class, token::validate);
  }

  @Test
  void validate_shouldThrow_whenTokenIsExpired() {
    var token = new PasswordResetToken();
    token.setRevoked(false);
    token.setExpiresAt(Instant.now().minusSeconds(60));

    assertThrows(PasswordResetTokenValidationException.class, token::validate);
  }
}
