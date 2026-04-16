package pl.sgorski.expense_splitter.features.auth.password_reset_token.service;

import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.exceptions.authentication.PasswordResetTokenNotFoundException;
import pl.sgorski.expense_splitter.features.auth.password_reset_token.config.PasswordResetProperties;
import pl.sgorski.expense_splitter.features.auth.password_reset_token.domain.PasswordResetToken;
import pl.sgorski.expense_splitter.features.auth.password_reset_token.repository.PasswordResetTokenRepository;
import pl.sgorski.expense_splitter.features.user.domain.User;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {

  private final PasswordResetProperties resetTokenProperties;
  private final PasswordResetTokenRepository resetTokenRepository;

  @Transactional
  public PasswordResetToken generatePasswordResetToken(User user) {
    var tokenUUID = UUID.randomUUID();
    var token =
        new PasswordResetToken(tokenUUID, user, resetTokenProperties.tokenExpirationTimeInMs());
    return resetTokenRepository.save(token);
  }

  @Transactional
  public void revokeAllUserTokens(UUID userId) {
    resetTokenRepository.revokeAllByUserId(userId);
  }

  @Transactional
  public void deleteInvalidTokens() {
    resetTokenRepository.deleteAllByExpiresAtBeforeOrIsRevokedTrue(Instant.now());
  }

  public PasswordResetToken getValidToken(UUID resetToken) {
    var token =
        resetTokenRepository
            .findByToken(resetToken)
            .orElseThrow(PasswordResetTokenNotFoundException::new);
    token.validate();
    return token;
  }
}
