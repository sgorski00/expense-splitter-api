package pl.sgorski.expense_splitter.IT.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.client.RestTestClient;
import pl.sgorski.expense_splitter.IT.base.IntegrationTest;
import pl.sgorski.expense_splitter.IT.base.factory.UserTestFactory;
import pl.sgorski.expense_splitter.features.auth.dto.request.GoogleAuthenticatorRequest;
import pl.sgorski.expense_splitter.features.auth.dto.request.LoginRequest;
import pl.sgorski.expense_splitter.features.auth.dto.response.LoginResponse;
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

  @Test
  void verify2Fa_shouldReturnVerifiedTokens_when2FaVerificationPassed() {
    var user =
        UserTestFactory.createUser(
            email,
            passwordEncoder.encode(rawPassword),
            true,
            secretEncryptor.encrypt(totpProperties.secret()));
    userRepository.save(user);

    var loginRequest = new LoginRequest(email, rawPassword);
    var code = String.valueOf(totpProperties.code());
    var twoFaRequest = new GoogleAuthenticatorRequest(code);
    var loginResponse = login(loginRequest);

    performTwoFaVerification(loginResponse.accessToken(), twoFaRequest)
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
    var user =
        UserTestFactory.createUser(
            email,
            passwordEncoder.encode(rawPassword),
            true,
            secretEncryptor.encrypt(totpProperties.secret()));
    userRepository.save(user);

    var loginRequest = new LoginRequest(email, rawPassword);
    var invalidCode = String.valueOf(totpProperties.code() + 1);
    var twoFaRequest = new GoogleAuthenticatorRequest(invalidCode);
    var loginResponse = login(loginRequest);

    performTwoFaVerification(loginResponse.accessToken(), twoFaRequest)
        .expectStatus()
        .isUnauthorized()
        .expectBody()
        .jsonPath("$.status")
        .isEqualTo(401)
        .jsonPath("$.title")
        .isNotEmpty();
  }

  @Test
  void verify2Fa_shouldReturn400_when2FaDisabled() {
    var user = UserTestFactory.createUser(email, passwordEncoder.encode(rawPassword), false, null);
    userRepository.save(user);

    var loginRequest = new LoginRequest(email, rawPassword);
    var code = String.valueOf(totpProperties.code());
    var twoFaRequest = new GoogleAuthenticatorRequest(code);
    var loginResponse = login(loginRequest);

    performTwoFaVerification(loginResponse.accessToken(), twoFaRequest)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.status")
        .isEqualTo(400)
        .jsonPath("$.title")
        .isNotEmpty();
  }

  @Test
  void verify2Fa_shouldReturn401_whenAccessTokenIsMissing() {
    var user = UserTestFactory.createUser(email, passwordEncoder.encode(rawPassword), false, null);
    userRepository.save(user);

    var code = String.valueOf(totpProperties.code());
    var twoFaRequest = new GoogleAuthenticatorRequest(code);

    performTwoFaVerification(null, twoFaRequest)
        .expectStatus()
        .isUnauthorized()
        .expectBody()
        .jsonPath("$.status")
        .isEqualTo(401)
        .jsonPath("$.title")
        .isNotEmpty();
  }

  @Test
  void verify2Fa_shouldReturn401_whenAccessTokenIsInvalid() {
    var user = UserTestFactory.createUser(email, passwordEncoder.encode(rawPassword), false, null);
    userRepository.save(user);

    var code = String.valueOf(totpProperties.code());
    var twoFaRequest = new GoogleAuthenticatorRequest(code);

    performTwoFaVerification("invalid-token", twoFaRequest)
        .expectStatus()
        .isUnauthorized()
        .expectBody()
        .jsonPath("$.status")
        .isEqualTo(401)
        .jsonPath("$.title")
        .isNotEmpty();
  }

  @Test
  void verify2Fa_shouldReturn400_when2FaCodeIsMalformed() {
    var user = UserTestFactory.createUser(email, passwordEncoder.encode(rawPassword), false, null);
    userRepository.save(user);

    var loginRequest = new LoginRequest(email, rawPassword);
    var twoFaRequest = new GoogleAuthenticatorRequest("abc");
    var loginResponse = login(loginRequest);

    performTwoFaVerification(loginResponse.accessToken(), twoFaRequest)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.status")
        .isEqualTo(400)
        .jsonPath("$.title")
        .isNotEmpty();
  }

  private LoginResponse login(LoginRequest request) {
    return restTestClient
        .post()
        .uri("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .exchange()
        .expectStatus()
        .isOk()
        .returnResult(LoginResponse.class)
        .getResponseBody();
  }

  private RestTestClient.ResponseSpec performTwoFaVerification(
      String accessToken, GoogleAuthenticatorRequest request) {
    return restTestClient
        .post()
        .uri("/auth/2fa/verify")
        .header("Authorization", "Bearer " + accessToken)
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .exchange();
  }
}
