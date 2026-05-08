package pl.sgorski.expense_splitter.IT.auth;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.bucket4j.Bucket;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.client.RestTestClient;
import pl.sgorski.expense_splitter.IT.base.IntegrationTest;
import pl.sgorski.expense_splitter.features.auth.dto.request.LoginRequest;
import pl.sgorski.expense_splitter.features.auth.two_fa.domain.UserTwoFactor;
import pl.sgorski.expense_splitter.features.auth.two_fa.repository.UserTwoFactorRepository;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.repository.UserRepository;
import pl.sgorski.expense_splitter.security.rate_limit.RateLimitType;

public class AuthLoginIT extends IntegrationTest {

  @Autowired private Flyway flyway;
  @Autowired private UserRepository userRepository;
  @Autowired private UserTwoFactorRepository userTwoFactorRepository;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired Cache<String, Bucket> rateLimitCache;

  private final String email = "user@example.com";
  private final String rawPassword = "P@ssword123";
  private User user;

  @BeforeEach
  void setUp() {
    rateLimitCache.invalidateAll();
    flyway.clean();
    flyway.migrate();

    user = new User();
    user.setEmail(email);
    user.setPasswordHash(passwordEncoder.encode(rawPassword));
    user.setPasswordForChange(false);
    userRepository.save(user);
  }

  @Test
  void login_shouldReturnAccessTokenAndRefreshToken_whenCredentialsAreValid() {
    var request = new LoginRequest(email, rawPassword);

    performLoginRequest(request)
        .expectStatus()
        .isOk()
        .expectHeader()
        .exists(HttpHeaders.SET_COOKIE)
        .expectBody()
        .jsonPath("$.accessToken")
        .isNotEmpty()
        .jsonPath("$.refreshToken")
        .isNotEmpty()
        .jsonPath("$.twoFactorRequired")
        .isEqualTo(false);
  }

  @Test
  void login_shouldReturnResponseWithTwoFaRequired_whenCredentialsAreValid() {
    var twoFa = new UserTwoFactor();
    twoFa.setSecret("SECRET");
    twoFa.setUser(user);
    twoFa.setEnabled(true);
    userTwoFactorRepository.save(twoFa);

    var request = new LoginRequest(email, rawPassword);

    performLoginRequest(request)
            .expectStatus()
            .isOk()
            .expectHeader()
            .exists(HttpHeaders.SET_COOKIE)
            .expectBody()
            .jsonPath("$.accessToken")
            .isNotEmpty()
            .jsonPath("$.refreshToken")
            .isNotEmpty()
            .jsonPath("$.twoFactorRequired")
            .isEqualTo(true);
  }

  @Test
  void login_shouldReturn401_whenCredentialsAreNotValid() {
    var request = new LoginRequest(email, "NotTh3Rig#tPassword");

    performLoginRequest(request)
            .expectStatus()
            .isUnauthorized()
            .expectHeader()
            .doesNotExist(HttpHeaders.SET_COOKIE)
            .expectBody()
            .jsonPath("$.status")
            .isEqualTo(401)
            .jsonPath("$.title")
            .isNotEmpty();
  }

  @Test
  void login_shouldReturn401_whenEmailIsNotValid() {
    var request = new LoginRequest("some-email@example.com", rawPassword);

    performLoginRequest(request)
            .expectStatus()
            .isUnauthorized()
            .expectHeader()
            .doesNotExist(HttpHeaders.SET_COOKIE)
            .expectBody()
            .jsonPath("$.status")
            .isEqualTo(401)
            .jsonPath("$.title")
            .isNotEmpty();
  }

  @Test
  void login_shouldReturn400_whenEmailIsWrongFormatted() {
    var request = new LoginRequest("not-an-email", rawPassword);

    performLoginRequest(request)
            .expectStatus()
            .isBadRequest()
            .expectHeader()
            .doesNotExist(HttpHeaders.SET_COOKIE)
            .expectBody()
            .jsonPath("$.status")
            .isEqualTo(400)
            .jsonPath("$.title")
            .isNotEmpty();
  }

  @Test
  void login_shouldReturn400_whenPasswordIsBlank() {
    var request = new LoginRequest(email, "   ");

    performLoginRequest(request)
            .expectStatus()
            .isBadRequest()
            .expectHeader()
            .doesNotExist(HttpHeaders.SET_COOKIE)
            .expectBody()
            .jsonPath("$.status")
            .isEqualTo(400)
            .jsonPath("$.title")
            .isNotEmpty();
  }

  @Test
  void login_shouldReturn429_whenRateLimitExceed() {
    var status = 429;
    var request = new LoginRequest(email, rawPassword);

    for (int i = 0; i < RateLimitType.AUTH.getLimit(); i++) {
      performLoginRequest(request);
    }

    performLoginRequest(request)
            .expectStatus()
            .isEqualTo(status);
  }

  private RestTestClient.ResponseSpec performLoginRequest(LoginRequest request) {
    return restTestClient
            .post()
            .uri("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .exchange();
  }
}
