package pl.sgorski.expense_splitter.IT.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.sgorski.expense_splitter.IT.base.IntegrationTest;
import pl.sgorski.expense_splitter.IT.base.factory.UserTestFactory;
import pl.sgorski.expense_splitter.IT.base.helper.AuthHelper;
import pl.sgorski.expense_splitter.features.auth.dto.request.GoogleAuthenticatorRequest;
import pl.sgorski.expense_splitter.features.auth.dto.request.LoginRequest;
import pl.sgorski.expense_splitter.features.auth.two_fa.config.TestTotpProperties;
import pl.sgorski.expense_splitter.features.auth.two_fa.service.SecretEncryptor;
import pl.sgorski.expense_splitter.features.user.repository.UserRepository;

public class AuthTwoFactorIT extends IntegrationTest {

  @Autowired private TestTotpProperties totpProperties;
  @Autowired private SecretEncryptor secretEncryptor;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private UserRepository userRepository;

  private final String email = "user@example.com";
  private final String rawPassword = "P@ssword123";

  @BeforeEach
  void setUp() {
    var user =
        UserTestFactory.createUserWithTwoFa(
            email,
            passwordEncoder.encode(rawPassword),
            secretEncryptor.encrypt(totpProperties.secret()));
    userRepository.save(user);
  }

  @Test
  void verify2Fa_shouldReturnVerifiedTokens_when2FaVerificationPassed() {
    var loginRequest = new LoginRequest(email, rawPassword);
    var accessToken = AuthHelper.obtainAccessToken(restTestClient, loginRequest);
    var code = String.valueOf(totpProperties.code());
    var twoFaRequest = new GoogleAuthenticatorRequest(code);

    AuthHelper.performTwoFaVerificationRequest(restTestClient, accessToken, twoFaRequest)
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.accessToken")
        .isNotEmpty()
        .jsonPath("$.refreshToken")
        .isNotEmpty();
  }

  @Test
  void verify2Fa_shouldReturn401_when2FaVerificationFailed() {
    var loginRequest = new LoginRequest(email, rawPassword);
    var accessToken = AuthHelper.obtainAccessToken(restTestClient, loginRequest);
    var invalidCode = String.valueOf(totpProperties.code() + 1);
    var twoFaRequest = new GoogleAuthenticatorRequest(invalidCode);

    AuthHelper.performTwoFaVerificationRequest(restTestClient, accessToken, twoFaRequest)
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  void verify2Fa_shouldReturn400_when2FaDisabled() {
    var userWithout2Fa = UserTestFactory.createUser(email, passwordEncoder.encode(rawPassword));
    userRepository.deleteAll();
    userRepository.save(userWithout2Fa);

    var loginRequest = new LoginRequest(email, rawPassword);
    var accessToken = AuthHelper.obtainAccessToken(restTestClient, loginRequest);
    var code = String.valueOf(totpProperties.code());
    var twoFaRequest = new GoogleAuthenticatorRequest(code);

    AuthHelper.performTwoFaVerificationRequest(restTestClient, accessToken, twoFaRequest)
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void verify2Fa_shouldReturn401_whenAccessTokenIsMissing() {
    var code = String.valueOf(totpProperties.code());
    var twoFaRequest = new GoogleAuthenticatorRequest(code);

    AuthHelper.performTwoFaVerificationRequest(restTestClient, null, twoFaRequest)
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  void verify2Fa_shouldReturn401_whenAccessTokenIsInvalid() {
    var code = String.valueOf(totpProperties.code());
    var twoFaRequest = new GoogleAuthenticatorRequest(code);

    AuthHelper.performTwoFaVerificationRequest(restTestClient, "invalid-token", twoFaRequest)
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  void verify2Fa_shouldReturn400_when2FaCodeIsMalformed() {
    var loginRequest = new LoginRequest(email, rawPassword);
    var accessToken = AuthHelper.obtainAccessToken(restTestClient, loginRequest);
    var twoFaRequest = new GoogleAuthenticatorRequest("abc");

    AuthHelper.performTwoFaVerificationRequest(restTestClient, accessToken, twoFaRequest)
        .expectStatus()
        .isBadRequest();
  }
}
