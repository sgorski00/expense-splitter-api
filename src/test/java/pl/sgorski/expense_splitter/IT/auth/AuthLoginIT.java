package pl.sgorski.expense_splitter.IT.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.sgorski.expense_splitter.IT.base.IntegrationTest;
import pl.sgorski.expense_splitter.IT.base.factory.UserTestFactory;
import pl.sgorski.expense_splitter.IT.base.helper.AuthHelper;
import pl.sgorski.expense_splitter.features.auth.dto.request.LoginRequest;
import pl.sgorski.expense_splitter.features.user.repository.UserRepository;
import pl.sgorski.expense_splitter.security.rate_limit.model.RateLimitType;

public class AuthLoginIT extends IntegrationTest {

  @Autowired private UserRepository userRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  private final String email = "user@example.com";
  private final String rawPassword = "P@ssword123";

  @BeforeEach
  void setUp() {
    var user = UserTestFactory.createUser(email, passwordEncoder.encode(rawPassword));
    userRepository.save(user);
  }

  @Test
  void login_shouldReturnAccessTokenAndRefreshToken_whenCredentialsAreValid() {
    var request = new LoginRequest(email, rawPassword);

    AuthHelper.performLoginRequest(restTestClient, request)
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
  void login_shouldReturnResponseWithTwoFaRequired_whenTwoFaIsEnabled() {
    var twoFaUserEmail = "2fa-user@example.com";
    var userWithTwoFa =
        UserTestFactory.createUserWithTwoFa(
            twoFaUserEmail, passwordEncoder.encode(rawPassword), "SECRET");
    userRepository.save(userWithTwoFa);

    var request = new LoginRequest(twoFaUserEmail, rawPassword);

    AuthHelper.performLoginRequest(restTestClient, request)
        .expectStatus()
        .isOk()
        .expectHeader()
        .doesNotExist(HttpHeaders.SET_COOKIE)
        .expectBody()
        .jsonPath("$.accessToken")
        .isNotEmpty()
        .jsonPath("$.refreshToken")
        .isEmpty()
        .jsonPath("$.twoFactorRequired")
        .isEqualTo(true);
  }

  @Test
  void login_shouldReturn401_whenCredentialsAreNotValid() {
    var request = new LoginRequest(email, "WrongPassword123");

    AuthHelper.performLoginRequest(restTestClient, request)
        .expectStatus()
        .isUnauthorized()
        .expectHeader()
        .doesNotExist(HttpHeaders.SET_COOKIE);
  }

  @Test
  void login_shouldReturn401_whenEmailIsNotValid() {
    var request = new LoginRequest("nonexistent@example.com", rawPassword);

    AuthHelper.performLoginRequest(restTestClient, request)
        .expectStatus()
        .isUnauthorized()
        .expectHeader()
        .doesNotExist(HttpHeaders.SET_COOKIE);
  }

  @Test
  void login_shouldReturn400_whenEmailIsWrongFormatted() {
    var request = new LoginRequest("not-an-email", rawPassword);

    AuthHelper.performLoginRequest(restTestClient, request)
        .expectStatus()
        .isBadRequest()
        .expectHeader()
        .doesNotExist(HttpHeaders.SET_COOKIE);
  }

  @Test
  void login_shouldReturn400_whenPasswordIsBlank() {
    var request = new LoginRequest(email, "   ");

    AuthHelper.performLoginRequest(restTestClient, request)
        .expectStatus()
        .isBadRequest()
        .expectHeader()
        .doesNotExist(HttpHeaders.SET_COOKIE);
  }

  @Test
  void login_shouldReturn429_whenRateLimitIsExceeded() {
    var request = new LoginRequest(email, rawPassword);

    for (int i = 0; i < RateLimitType.AUTH.getLimit(); i++) {
      AuthHelper.performLoginRequest(restTestClient, request);
    }

    AuthHelper.performLoginRequest(restTestClient, request).expectStatus().isEqualTo(429);
  }
}
