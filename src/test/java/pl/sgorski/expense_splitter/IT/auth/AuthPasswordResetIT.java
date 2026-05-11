package pl.sgorski.expense_splitter.IT.auth;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.sgorski.expense_splitter.IT.base.IntegrationTest;
import pl.sgorski.expense_splitter.IT.base.factory.UserTestFactory;
import pl.sgorski.expense_splitter.IT.base.helper.AuthHelper;
import pl.sgorski.expense_splitter.features.auth.dto.request.ConfirmPasswordResetRequest;
import pl.sgorski.expense_splitter.features.auth.dto.request.PasswordResetRequest;
import pl.sgorski.expense_splitter.features.auth.password_reset_token.repository.PasswordResetTokenRepository;
import pl.sgorski.expense_splitter.features.user.repository.UserRepository;
import pl.sgorski.expense_splitter.security.rate_limit.RateLimitType;

public class AuthPasswordResetIT extends IntegrationTest {

  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private UserRepository userRepository;
  @Autowired private PasswordResetTokenRepository tokenRepository;

  private String email;
  private String newRawPassword;

  @BeforeEach
  void setUp() {
    email = "user@example.com";
    newRawPassword = "NewPassword123!";

    var user = UserTestFactory.createUser(email, passwordEncoder.encode("OldPassword123!"));
    userRepository.save(user);
  }

  @Test
  void resetPassword_shouldReturn204_whenEmailIsValid() {
    var request = new PasswordResetRequest(email);

    AuthHelper.performPasswordResetRequest(restTestClient, request).expectStatus().isNoContent();
  }

  @Test
  void resetPassword_shouldReturn204_whenEmailDoesNotExist() {
    var request = new PasswordResetRequest("other@example.com");

    AuthHelper.performPasswordResetRequest(restTestClient, request).expectStatus().isNoContent();
  }

  @Test
  void resetPassword_shouldReturn400_whenEmailIsMalformed() {
    var request = new PasswordResetRequest("not-an-email");

    AuthHelper.performPasswordResetRequest(restTestClient, request).expectStatus().isBadRequest();
  }

  @Test
  void resetPassword_shouldApplyRateLimit_whenTooManyRequestsSent() {
    var request = new PasswordResetRequest(email);
    for (int i = 0; i < RateLimitType.AUTH.getLimit(); i++) {
      AuthHelper.performPasswordResetRequest(restTestClient, request).expectStatus().isNoContent();
    }

    AuthHelper.performPasswordResetRequest(restTestClient, request).expectStatus().isEqualTo(429);
  }

  @Test
  void confirmResetPassword_shouldReturn204_whenTokenAndPasswordAreValid() {
    AuthHelper.performPasswordResetRequest(restTestClient, new PasswordResetRequest(email))
        .expectStatus()
        .isNoContent();
    var token = tokenRepository.findAll().getFirst().getToken();
    var request = new ConfirmPasswordResetRequest(token, newRawPassword, newRawPassword);

    AuthHelper.performConfirmPasswordResetRequest(restTestClient, request)
        .expectStatus()
        .isNoContent();
  }

  @Test
  void confirmResetPassword_shouldReturn404_whenTokenIsNotFound() {
    var token = UUID.randomUUID();
    var request = new ConfirmPasswordResetRequest(token, newRawPassword, newRawPassword);

    AuthHelper.performConfirmPasswordResetRequest(restTestClient, request)
        .expectStatus()
        .isNotFound();
  }

  @Test
  void confirmResetPassword_shouldReturn400_whenTokenIsExpired() {
    AuthHelper.performPasswordResetRequest(restTestClient, new PasswordResetRequest(email))
        .expectStatus()
        .isNoContent();
    var token = tokenRepository.findAll().getFirst();
    token.setExpiresAt(Instant.now().minusSeconds(1));
    tokenRepository.save(token);

    var request = new ConfirmPasswordResetRequest(token.getToken(), newRawPassword, newRawPassword);

    AuthHelper.performConfirmPasswordResetRequest(restTestClient, request)
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void confirmResetPassword_shouldReturn400_whenPasswordIsTooWeak() {
    AuthHelper.performPasswordResetRequest(restTestClient, new PasswordResetRequest(email))
        .expectStatus()
        .isNoContent();
    var weakPassword = "weak";
    var token = tokenRepository.findAll().getFirst().getToken();
    var request = new ConfirmPasswordResetRequest(token, weakPassword, weakPassword);

    AuthHelper.performConfirmPasswordResetRequest(restTestClient, request)
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void confirmResetPassword_shouldReturn400_whenPasswordsDoNotMatch() {
    AuthHelper.performPasswordResetRequest(restTestClient, new PasswordResetRequest(email))
        .expectStatus()
        .isNoContent();
    var token = tokenRepository.findAll().getFirst().getToken();
    var request =
        new ConfirmPasswordResetRequest(token, newRawPassword, newRawPassword + "different");

    AuthHelper.performConfirmPasswordResetRequest(restTestClient, request)
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void confirmResetPassword_shouldReturn400_whenUsingRevokedToken() {
    AuthHelper.performPasswordResetRequest(restTestClient, new PasswordResetRequest(email))
        .expectStatus()
        .isNoContent();
    var token = tokenRepository.findAll().getFirst().getToken();
    var request = new ConfirmPasswordResetRequest(token, newRawPassword, newRawPassword);

    AuthHelper.performConfirmPasswordResetRequest(restTestClient, request)
        .expectStatus()
        .isNoContent();
    AuthHelper.performConfirmPasswordResetRequest(restTestClient, request)
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void confirmResetPassword_shouldChangePasswordInDatabase_whenRequestIsValid() {
    AuthHelper.performPasswordResetRequest(restTestClient, new PasswordResetRequest(email))
        .expectStatus()
        .isNoContent();
    var token = tokenRepository.findAll().getFirst().getToken();
    var request = new ConfirmPasswordResetRequest(token, newRawPassword, newRawPassword);

    AuthHelper.performConfirmPasswordResetRequest(restTestClient, request)
        .expectStatus()
        .isNoContent();

    var user = userRepository.findByEmailAndDeletedAtIsNull(email).orElseThrow();
    assertTrue(passwordEncoder.matches(newRawPassword, user.getPasswordHash()));
  }
}
