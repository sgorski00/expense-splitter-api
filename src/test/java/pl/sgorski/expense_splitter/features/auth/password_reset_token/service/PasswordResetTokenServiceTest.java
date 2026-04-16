package pl.sgorski.expense_splitter.features.auth.password_reset_token.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.expense_splitter.exceptions.authentication.PasswordResetTokenNotFoundException;
import pl.sgorski.expense_splitter.features.auth.password_reset_token.config.PasswordResetProperties;
import pl.sgorski.expense_splitter.features.auth.password_reset_token.domain.PasswordResetToken;
import pl.sgorski.expense_splitter.features.auth.password_reset_token.repository.PasswordResetTokenRepository;
import pl.sgorski.expense_splitter.features.user.domain.User;

@ExtendWith(MockitoExtension.class)
public class PasswordResetTokenServiceTest {

  @Mock private PasswordResetProperties resetTokenProperties;
  @Mock private PasswordResetTokenRepository resetTokenRepository;
  @InjectMocks private PasswordResetTokenService passwordResetTokenService;

  @Test
  void generatePasswordResetToken_shouldSaveAndReturnTokenForUser_correctRequest() {
    var user = new User();
    when(resetTokenProperties.tokenExpirationTimeInMs()).thenReturn(60000L);
    when(resetTokenRepository.save(any(PasswordResetToken.class)))
        .thenReturn(new PasswordResetToken());

    var token = passwordResetTokenService.generatePasswordResetToken(user);

    assertNotNull(token);
    verify(resetTokenRepository, times(1)).save(any(PasswordResetToken.class));
    verifyNoMoreInteractions(resetTokenRepository);
  }

  @Test
  void revokeAllUserTokens_shouldCallRepositoryMethod_withCorrectUserId() {
    var userId = UUID.randomUUID();

    passwordResetTokenService.revokeAllUserTokens(userId);

    verify(resetTokenRepository, times(1)).revokeAllByUserId(userId);
    verifyNoMoreInteractions(resetTokenRepository);
  }

  @Test
  void deleteInvalidTokens_shouldCallRepositoryWithCurrentInstant() {
    passwordResetTokenService.deleteInvalidTokens();

    verify(resetTokenRepository, times(1))
        .deleteAllByExpiresAtBeforeOrIsRevokedTrue(any(Instant.class));
    verifyNoMoreInteractions(resetTokenRepository);
  }

  @Test
  void getToken_shouldReturnToken_whenExists() {
    var tokenValue = UUID.randomUUID();
    var token = new PasswordResetToken();
    when(resetTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(token));

    var result = passwordResetTokenService.getToken(tokenValue);

    assertEquals(token, result);
    verify(resetTokenRepository, times(1)).findByToken(tokenValue);
    verifyNoMoreInteractions(resetTokenRepository);
  }

  @Test
  void getToken_shouldThrowNotFoundException_whenTokenDoesNotExist() {
    var tokenValue = UUID.randomUUID();
    when(resetTokenRepository.findByToken(tokenValue)).thenReturn(Optional.empty());

    assertThrows(
        PasswordResetTokenNotFoundException.class,
        () -> passwordResetTokenService.getToken(tokenValue));

    verify(resetTokenRepository, times(1)).findByToken(tokenValue);
    verifyNoMoreInteractions(resetTokenRepository);
  }
}
