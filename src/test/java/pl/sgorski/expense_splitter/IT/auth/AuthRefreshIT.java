package pl.sgorski.expense_splitter.IT.auth;

import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.client.RestTestClient;
import pl.sgorski.expense_splitter.IT.base.IntegrationTest;
import pl.sgorski.expense_splitter.IT.base.factory.UserTestFactory;
import pl.sgorski.expense_splitter.features.auth.dto.request.LoginRequest;
import pl.sgorski.expense_splitter.features.auth.dto.response.LoginResponse;
import pl.sgorski.expense_splitter.features.user.repository.UserRepository;

public class AuthRefreshIT extends IntegrationTest {

  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private UserRepository userRepository;

  private final String email = "user@example.com";
  private final String rawPassword = "P@ssword123";

  @Test
  void refreshToken_shouldReturnNewTokens_whenRefreshTokenIsValidFromCookie() {
    var user = UserTestFactory.createUser(email, passwordEncoder.encode(rawPassword), false, null);
    userRepository.save(user);
    var loginRequest = new LoginRequest(email, rawPassword);
    var loginResponse = login(loginRequest);
    var refreshToken = Objects.requireNonNull(loginResponse.refreshToken()).toString();

    restTestClient
        .post()
        .uri("/auth/refresh")
        .cookie("refreshToken", refreshToken)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.refreshToken")
        .isNotEmpty()
        .jsonPath("$.accessToken")
        .isNotEmpty()
        .jsonPath("$.twoFactorRequired")
        .isEqualTo(false);
  }

  @Test
  void refreshToken_shouldReturnNewTokens_whenRefreshTokenIsValidFromHeader() {
    var user = UserTestFactory.createUser(email, passwordEncoder.encode(rawPassword), false, null);
    userRepository.save(user);
    var loginRequest = new LoginRequest(email, rawPassword);
    var loginResponse = login(loginRequest);
    var refreshToken = Objects.requireNonNull(loginResponse.refreshToken()).toString();

    performRefreshRequest(refreshToken)
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.refreshToken")
        .isNotEmpty()
        .jsonPath("$.accessToken")
        .isNotEmpty()
        .jsonPath("$.twoFactorRequired")
        .isEqualTo(false);
  }

  @Test
  void refreshToken_shouldReturn401_whenRefreshTokenIsNotPassed() {
    restTestClient
        .post()
        .uri("/auth/refresh")
        .exchange()
        .expectStatus()
        .isUnauthorized()
        .expectBody()
        .jsonPath("$.title")
        .isNotEmpty()
        .jsonPath("$.status")
        .isEqualTo(401);
  }

  @Test
  void refreshToken_shouldReturn401_whenRefreshTokenIsMalformed() {
    performRefreshRequest("Bearer malformed-refresh-token")
        .expectStatus()
        .isUnauthorized()
        .expectBody()
        .jsonPath("$.title")
        .isNotEmpty()
        .jsonPath("$.status")
        .isEqualTo(401);
  }

  @Test
  void refreshToken_shouldReturn404_whenRefreshTokenIsNotFound() {
    performRefreshRequest(UUID.randomUUID().toString())
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.title")
        .isNotEmpty()
        .jsonPath("$.status")
        .isEqualTo(404);
  }

  @Test
  void refreshToken_shouldReturn401_whenTryingToUseSameTokenAnotherTime() {
    var user = UserTestFactory.createUser(email, passwordEncoder.encode(rawPassword), false, null);
    userRepository.save(user);
    var loginRequest = new LoginRequest(email, rawPassword);
    var loginResponse = login(loginRequest);
    var refreshToken = Objects.requireNonNull(loginResponse.refreshToken()).toString();

    performRefreshRequest(refreshToken).expectStatus().isOk().returnResult(LoginResponse.class);

    performRefreshRequest(refreshToken)
        .expectStatus()
        .isUnauthorized()
        .expectBody()
        .jsonPath("$.title")
        .isNotEmpty()
        .jsonPath("$.status")
        .isEqualTo(401);
  }

  private RestTestClient.ResponseSpec performRefreshRequest(String refreshToken) {
    return restTestClient
        .post()
        .uri("/auth/refresh")
        .header("Authorization", "Bearer " + refreshToken)
        .exchange();
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
}
