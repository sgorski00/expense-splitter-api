package pl.sgorski.expense_splitter.features.auth.two_fa.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.expense_splitter.exceptions.authentication.two_fa.TwoFactorAlreadySetupException;
import pl.sgorski.expense_splitter.exceptions.authentication.two_fa.TwoFactorNotSetupException;
import pl.sgorski.expense_splitter.exceptions.authentication.two_fa.TwoFactorVerificationFailedException;
import pl.sgorski.expense_splitter.features.auth.dto.request.GoogleAuthenticatorRequest;
import pl.sgorski.expense_splitter.features.auth.two_fa.domain.UserTwoFactor;
import pl.sgorski.expense_splitter.features.auth.two_fa.repository.UserTwoFactorRepository;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserTwoFactorServiceTest {

  @Mock private TotpService totpService;
  @Mock private UserService userService;
  @Mock private UserTwoFactorRepository userTwoFactorRepository;
  @Mock private SecretEncryptor secretEncryptor;

  @InjectMocks private UserTwoFactorService userTwoFactorService;

  private UUID userId;
  private User user;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    user = new User();
    user.setId(userId);
    user.setEmail("test@example.com");
  }

  @Test
  void setup2FA_shouldReturnQrCodeUrl_when2FANotSetup() {
    var plainSecret = "PLAIN_SECRET";
    var encryptedSecret = "ENCRYPTED_SECRET";
    var qrCodeUrl = "otpauth://totp/test@example.com?secret=PLAIN_SECRET".getBytes();

    when(userService.getUser(userId)).thenReturn(user);
    when(totpService.generateSecret()).thenReturn(plainSecret);
    when(secretEncryptor.encrypt(plainSecret)).thenReturn(encryptedSecret);
    when(totpService.buildOtpAuthUrl(user.getEmail(), plainSecret)).thenReturn(qrCodeUrl);

    var result = userTwoFactorService.setup2FA(userId);

    assertArrayEquals(qrCodeUrl, result);
    verify(userTwoFactorRepository, times(1)).save(any(UserTwoFactor.class));
    assertNotNull(user.getTwoFactor());
    assertEquals(encryptedSecret, user.getTwoFactor().getSecret());
    assertFalse(user.getTwoFactor().isEnabled());
  }

  @Test
  void setup2FA_shouldThrowException_when2FAAlreadySetup() {
    user.setTwoFactor(new UserTwoFactor());
    when(userService.getUser(userId)).thenReturn(user);

    assertThrows(TwoFactorAlreadySetupException.class, () -> userTwoFactorService.setup2FA(userId));

    verifyNoInteractions(totpService, secretEncryptor, userTwoFactorRepository);
  }

  @Test
  void confirm2FA_shouldEnable2FA_whenCodeIsValid() {
    var code = "123456";
    var encryptedSecret = "ENCRYPTED_SECRET";
    var plainSecret = "PLAIN_SECRET";
    var tf = new UserTwoFactor();
    tf.setSecret(encryptedSecret);

    when(userTwoFactorRepository.findByUserId(userId)).thenReturn(Optional.of(tf));
    when(secretEncryptor.decrypt(encryptedSecret)).thenReturn(plainSecret);
    when(totpService.verify(plainSecret, 123456)).thenReturn(true);

    userTwoFactorService.confirm2FA(userId, code);

    assertTrue(tf.isEnabled());
    // log.info("2FA confirmed for user: {}", userId);
  }

  @Test
  void confirm2FA_shouldThrowException_when2FANotSetup() {
    when(userTwoFactorRepository.findByUserId(userId)).thenReturn(Optional.empty());

    assertThrows(
        TwoFactorNotSetupException.class, () -> userTwoFactorService.confirm2FA(userId, "123456"));
  }

  @Test
  void confirm2FA_shouldThrowException_whenSecretIsMissing() {
    var tf = new UserTwoFactor();
    tf.setSecret(null);
    when(userTwoFactorRepository.findByUserId(userId)).thenReturn(Optional.of(tf));

    assertThrows(
        TwoFactorNotSetupException.class, () -> userTwoFactorService.confirm2FA(userId, "123456"));
  }

  @Test
  void confirm2FA_shouldThrowException_whenCodeIsInvalid() {
    var code = "123456";
    var encryptedSecret = "ENCRYPTED_SECRET";
    var plainSecret = "PLAIN_SECRET";
    var tf = new UserTwoFactor();
    tf.setSecret(encryptedSecret);

    when(userTwoFactorRepository.findByUserId(userId)).thenReturn(Optional.of(tf));
    when(secretEncryptor.decrypt(encryptedSecret)).thenReturn(plainSecret);
    when(totpService.verify(plainSecret, 123456)).thenReturn(false);

    assertThrows(
        TwoFactorVerificationFailedException.class,
        () -> userTwoFactorService.confirm2FA(userId, code));
    assertFalse(tf.isEnabled());
  }

  @Test
  void disable2FA_shouldDelete2FA_whenSetup() {
    var tf = new UserTwoFactor();
    user.setTwoFactor(tf);
    when(userService.getUser(userId)).thenReturn(user);

    userTwoFactorService.disable2FA(userId);

    verify(userTwoFactorRepository, times(1)).delete(tf);
    assertNull(user.getTwoFactor());
  }

  @Test
  void disable2FA_shouldThrowException_when2FANotSetup() {
    user.setTwoFactor(null);
    when(userService.getUser(userId)).thenReturn(user);

    assertThrows(TwoFactorNotSetupException.class, () -> userTwoFactorService.disable2FA(userId));
    verify(userTwoFactorRepository, never()).delete(any());
  }

  @Test
  void verify2FA_shouldSucceed_whenCodeIsValid() {
    var code = "123456";
    var request = new GoogleAuthenticatorRequest(code);
    var encryptedSecret = "ENCRYPTED_SECRET";
    var plainSecret = "PLAIN_SECRET";
    var tf = new UserTwoFactor();
    tf.setEnabled(true);
    tf.setSecret(encryptedSecret);

    when(userTwoFactorRepository.findByUserId(userId)).thenReturn(Optional.of(tf));
    when(secretEncryptor.decrypt(encryptedSecret)).thenReturn(plainSecret);
    when(totpService.verify(plainSecret, 123456)).thenReturn(true);

    assertDoesNotThrow(() -> userTwoFactorService.verify2FA(userId, request));
  }

  @Test
  void verify2FA_shouldThrowException_when2FANotSetup() {
    when(userTwoFactorRepository.findByUserId(userId)).thenReturn(Optional.empty());

    assertThrows(
        TwoFactorNotSetupException.class,
        () -> userTwoFactorService.verify2FA(userId, new GoogleAuthenticatorRequest("123456")));
  }

  @Test
  void verify2FA_shouldThrowException_whenNotEnabled() {
    var tf = new UserTwoFactor();
    tf.setEnabled(false);
    when(userTwoFactorRepository.findByUserId(userId)).thenReturn(Optional.of(tf));

    assertThrows(
        TwoFactorNotSetupException.class,
        () -> userTwoFactorService.verify2FA(userId, new GoogleAuthenticatorRequest("123456")));
  }

  @Test
  void verify2FA_shouldThrowException_whenSecretIsMissing() {
    var tf = new UserTwoFactor();
    tf.setEnabled(true);
    tf.setSecret(null);
    when(userTwoFactorRepository.findByUserId(userId)).thenReturn(Optional.of(tf));

    assertThrows(
        TwoFactorNotSetupException.class,
        () -> userTwoFactorService.verify2FA(userId, new GoogleAuthenticatorRequest("123456")));
  }

  @Test
  void verify2FA_shouldThrowException_whenCodeIsInvalid() {
    var code = "123456";
    var request = new GoogleAuthenticatorRequest(code);
    var encryptedSecret = "ENCRYPTED_SECRET";
    var plainSecret = "PLAIN_SECRET";
    var tf = new UserTwoFactor();
    tf.setEnabled(true);
    tf.setSecret(encryptedSecret);

    when(userTwoFactorRepository.findByUserId(userId)).thenReturn(Optional.of(tf));
    when(secretEncryptor.decrypt(encryptedSecret)).thenReturn(plainSecret);
    when(totpService.verify(plainSecret, 123456)).thenReturn(false);

    assertThrows(
        TwoFactorVerificationFailedException.class,
        () -> userTwoFactorService.verify2FA(userId, request));
  }
}
