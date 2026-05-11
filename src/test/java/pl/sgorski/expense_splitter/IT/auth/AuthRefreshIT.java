package pl.sgorski.expense_splitter.IT.auth;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.sgorski.expense_splitter.IT.base.IntegrationTest;
import pl.sgorski.expense_splitter.IT.base.factory.UserTestFactory;
import pl.sgorski.expense_splitter.IT.base.helper.AuthHelper;
import pl.sgorski.expense_splitter.features.auth.dto.request.LoginRequest;
import pl.sgorski.expense_splitter.features.user.repository.UserRepository;

public class AuthRefreshIT extends IntegrationTest {

  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private UserRepository userRepository;

  private final String email = "user@example.com";
  private final String rawPassword = "P@ssword123";

  @BeforeEach
  void setUp() {
    var user = UserTestFactory.createUser(email, passwordEncoder.encode(rawPassword));
    userRepository.save(user);
  }

  @Test
  void refreshToken_shouldReturnNewTokens_whenRefreshTokenIsValidFromCookie() {
    var loginRequest = new LoginRequest(email, rawPassword);
    var refreshToken = AuthHelper.obtainRefreshToken(restTestClient, loginRequest).toString();

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
    var loginRequest = new LoginRequest(email, rawPassword);
    var refreshToken = AuthHelper.obtainRefreshToken(restTestClient, loginRequest).toString();

    AuthHelper.performRefreshRequest(restTestClient, refreshToken)
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
    restTestClient.post().uri("/auth/refresh").exchange().expectStatus().isUnauthorized();
  }

  @Test
  void refreshToken_shouldReturn401_whenRefreshTokenIsMalformed() {
    AuthHelper.performRefreshRequest(restTestClient, "malformed-refresh-token")
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  void refreshToken_shouldReturn404_whenRefreshTokenIsNotFound() {
    AuthHelper.performRefreshRequest(restTestClient, UUID.randomUUID().toString())
        .expectStatus()
        .isNotFound();
  }

  @Test
  void refreshToken_shouldReturn401_whenTryingToUseSameTokenAnotherTime() {
    var loginRequest = new LoginRequest(email, rawPassword);
    var refreshToken = AuthHelper.obtainRefreshToken(restTestClient, loginRequest).toString();

    AuthHelper.performRefreshRequest(restTestClient, refreshToken).expectStatus().isOk();

    AuthHelper.performRefreshRequest(restTestClient, refreshToken).expectStatus().isUnauthorized();
  }
}
