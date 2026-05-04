package pl.sgorski.expense_splitter.features.auth.two_fa.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sgorski.expense_splitter.exceptions.authentication.TwoFactorAlreadySetupException;
import pl.sgorski.expense_splitter.exceptions.authentication.TwoFactorNotSetupException;
import pl.sgorski.expense_splitter.exceptions.authentication.TwoFactorVerificationFailedException;
import pl.sgorski.expense_splitter.features.auth.two_fa.domain.UserTwoFactor;
import pl.sgorski.expense_splitter.features.auth.two_fa.repository.UserTwoFactorRepository;
import pl.sgorski.expense_splitter.features.user.service.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserTwoFactorService {

  private final TotpService totpService;
  private final UserService userService;
  private final UserTwoFactorRepository userTwoFactorRepository;
  private final SecretEncryptor secretEncryptor;

  @Transactional
  public byte[] setup2FA(UUID userId) {
    var user = userService.getUser(userId);
    if (user.getTwoFactor() != null) {
      throw new TwoFactorAlreadySetupException("2FA is already set up for this user");
    }
    var plainSecret = totpService.generateSecret();
    var encryptedSecret = secretEncryptor.encrypt(plainSecret);
    var tf = new UserTwoFactor();
    tf.setUser(user);
    tf.setEnabled(false);
    tf.setSecret(encryptedSecret);
    user.setTwoFactor(tf);
    userTwoFactorRepository.save(tf);
    log.debug("2FA setup initiated for user: {}", userId);
    return totpService.buildOtpAuthUrl(user.getEmail(), plainSecret);
  }

  @Transactional
  public void confirm2FA(UUID userId, String code) {
    var codeInt = Integer.parseInt(code);
    var tf =
        userTwoFactorRepository
            .findByUserId(userId)
            .orElseThrow(() -> new TwoFactorNotSetupException("2FA is not set up for this user"));
    if (tf.getSecret() == null) {
      throw new TwoFactorNotSetupException("2FA secret is missing for this user");
    }
    var secret = secretEncryptor.decrypt(tf.getSecret());
    if (!totpService.verify(secret, codeInt)) {
      throw new TwoFactorVerificationFailedException("Invalid 2FA code");
    }
    tf.setEnabled(true);
    log.info("2FA confirmed for user: {}", userId);
  }

  @Transactional
  public void disable2FA(UUID userId) {
    var user = userService.getUser(userId);
    var tf = user.getTwoFactor();
    if (tf == null) {
      throw new TwoFactorNotSetupException("2FA is not set up for this user");
    }
    userTwoFactorRepository.delete(tf);
    user.setTwoFactor(null);
    log.info("2FA disabled for user: {}", userId);
  }
}
