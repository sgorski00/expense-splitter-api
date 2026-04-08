package pl.sgorski.expense_splitter.features.auth.refresh_token.service;

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
import pl.sgorski.expense_splitter.exceptions.NotFoundException;
import pl.sgorski.expense_splitter.features.auth.refresh_token.config.RefreshTokenProperties;
import pl.sgorski.expense_splitter.features.auth.refresh_token.domain.RefreshToken;
import pl.sgorski.expense_splitter.features.auth.refresh_token.repository.RefreshTokenRepository;
import pl.sgorski.expense_splitter.features.user.domain.User;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

  @Mock private RefreshTokenProperties refreshTokenProperties;
  @Mock private RefreshTokenRepository refreshTokenRepository;
  @InjectMocks private RefreshTokenService refreshTokenService;

  @Test
  void generateRefreshToken_shouldSaveAndReturnTokenForUser_correctRequest() {
    var user = new User();
    when(refreshTokenProperties.expirationTimeInMs()).thenReturn(60000L);
    when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(new RefreshToken());

    var token = refreshTokenService.generateRefreshToken(user);

    assertNotNull(token);
    verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    verifyNoMoreInteractions(refreshTokenRepository);
  }

  @Test
  void revokeToken_shouldRevokeExistingToken_tokenFound() {
    var tokenValue = UUID.randomUUID();
    var token = new RefreshToken();
    when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(token));

    refreshTokenService.revokeToken(tokenValue);

    assert (token.isRevoked());
    verify(refreshTokenRepository, times(1)).save(token);
    verifyNoMoreInteractions(refreshTokenRepository);
  }

  @Test
  void revokeToken_shouldThrowNotFoundException_tokenNotFound() {
    var tokenValue = UUID.randomUUID();
    when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> refreshTokenService.revokeToken(tokenValue));
    verify(refreshTokenRepository, times(1)).findByToken(tokenValue);
    verifyNoMoreInteractions(refreshTokenRepository);
  }

  @Test
  void revokeAllUserTokens_shouldCallRepositoryMethod_withCorrectUserId() {
    var userId = UUID.randomUUID();

    refreshTokenService.revokeAllUserTokens(userId);

    verify(refreshTokenRepository, times(1)).revokeAllByUserId(userId);
    verifyNoMoreInteractions(refreshTokenRepository);
  }

  @Test
  void getExpirationSecond_shouldReturnCorrectValue_inSeconds() {
    when(refreshTokenProperties.expirationTimeInMs()).thenReturn(30000L);

    var expirationSeconds = refreshTokenService.getExpirationSecond();

    assertEquals(30, expirationSeconds);
    verify(refreshTokenProperties, times(1)).expirationTimeInMs();
    verifyNoMoreInteractions(refreshTokenProperties);
  }

  @Test
  void deletedInvalidTokens_shouldCallRepositoryWithCurrentInstant() {
    refreshTokenService.deletedInvalidTokens();

    verify(refreshTokenRepository, times(1))
        .deleteAllByExpiresAtBeforeOrIsRevokedTrue(any(Instant.class));
    verifyNoMoreInteractions(refreshTokenRepository);
  }

  @Test
  void getToken_shouldReturnToken_whenExists() {
    var tokenValue = UUID.randomUUID();
    var token = new RefreshToken();
    when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(token));

    var result = refreshTokenService.getToken(tokenValue);

    assertEquals(token, result);
    verify(refreshTokenRepository, times(1)).findByToken(tokenValue);
    verifyNoMoreInteractions(refreshTokenRepository);
  }

  @Test
  void getToken_shouldThrowNotFoundException_whenTokenDoesNotExist() {
    var tokenValue = UUID.randomUUID();
    when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> refreshTokenService.getToken(tokenValue));

    verify(refreshTokenRepository, times(1)).findByToken(tokenValue);
    verifyNoMoreInteractions(refreshTokenRepository);
  }
}
