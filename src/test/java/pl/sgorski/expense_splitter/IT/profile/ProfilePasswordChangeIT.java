package pl.sgorski.expense_splitter.IT.profile;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.sgorski.expense_splitter.IT.base.IntegrationTest;
import pl.sgorski.expense_splitter.IT.base.factory.UserTestFactory;
import pl.sgorski.expense_splitter.IT.base.helper.AuthHelper;
import pl.sgorski.expense_splitter.IT.base.helper.ProfileHelper;
import pl.sgorski.expense_splitter.features.auth.dto.request.LoginRequest;
import pl.sgorski.expense_splitter.features.auth.dto.response.LoginResponse;
import pl.sgorski.expense_splitter.features.user.dto.request.PasswordChangeRequest;
import pl.sgorski.expense_splitter.features.user.dto.request.PasswordSetRequest;
import pl.sgorski.expense_splitter.features.user.repository.UserRepository;

public class ProfilePasswordChangeIT extends IntegrationTest {

  private static final String EMAIL = "user1@example.com";
  private static final String PASSWORD = "Us3rr@2026";
  private static final String NEW_PASSWORD = "NewP@ssw0rd";

  @Autowired private UserRepository userRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {
    var user = UserTestFactory.createUser(EMAIL, passwordEncoder.encode(PASSWORD));
    userRepository.save(user);
  }

  @Test
  void shouldNotAllowToSetPassword_whenPasswordIsNotNull() {
    var request = new PasswordSetRequest(NEW_PASSWORD, NEW_PASSWORD);
    var loginRequest = new LoginRequest(EMAIL, PASSWORD);
    var accessToken = AuthHelper.obtainAccessToken(restTestClient, loginRequest);

    ProfileHelper.performSetPasswordRequest(restTestClient, request, accessToken)
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void shouldNotAllowToSetPassword_whenBearerTokenIsInvalid() {
    var request = new PasswordSetRequest(NEW_PASSWORD, NEW_PASSWORD);

    ProfileHelper.performSetPasswordRequest(restTestClient, request, "invalid")
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  void shouldNotAllowToSetPassword_whenBearerTokenIsMissing() {
    var request = new PasswordSetRequest(NEW_PASSWORD, NEW_PASSWORD);

    ProfileHelper.performSetPasswordRequest(restTestClient, request, null)
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  void shouldNotAllowToChangePassword_whenBearerTokenIsInvalid() {
    var request = new PasswordChangeRequest(PASSWORD, NEW_PASSWORD, NEW_PASSWORD);

    ProfileHelper.performChangePasswordRequest(restTestClient, request, "invalid")
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  void shouldNotAllowToChangePassword_whenBearerTokenIsMissing() {
    var request = new PasswordChangeRequest(PASSWORD, NEW_PASSWORD, NEW_PASSWORD);

    ProfileHelper.performChangePasswordRequest(restTestClient, request, null)
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  void shouldChangePassword_whenRequestIsValid() {
    var request = new PasswordChangeRequest(PASSWORD, NEW_PASSWORD, NEW_PASSWORD);
    var loginRequest = new LoginRequest(EMAIL, PASSWORD);
    var accessToken = AuthHelper.obtainAccessToken(restTestClient, loginRequest);

    ProfileHelper.performChangePasswordRequest(restTestClient, request, accessToken)
        .expectStatus()
        .isOk()
        .expectHeader()
        .exists(HttpHeaders.SET_COOKIE)
        .expectBody()
        .jsonPath("$.accessToken")
        .isNotEmpty()
        .jsonPath("$.refreshToken")
        .isNotEmpty();

    var user = userRepository.findByEmailAndDeletedAtIsNull(EMAIL).orElseThrow();
    assertTrue(passwordEncoder.matches(NEW_PASSWORD, user.getPasswordHash()));
  }

  @Test
  void shouldChangePassword_whenRequestIsValidAndUserPasswordIsMarkedToChange() {
    var email2 = "user2@example.com";
    var userForChange =
        UserTestFactory.createUserWithPasswordChange(
            email2, passwordEncoder.encode(PASSWORD), true);
    userRepository.save(userForChange);

    var request = new PasswordChangeRequest(PASSWORD, NEW_PASSWORD, NEW_PASSWORD);
    var loginRequest = new LoginRequest(email2, PASSWORD);
    var accessToken = AuthHelper.obtainAccessToken(restTestClient, loginRequest);

    ProfileHelper.performChangePasswordRequest(restTestClient, request, accessToken)
        .expectStatus()
        .isOk()
        .expectHeader()
        .exists(HttpHeaders.SET_COOKIE)
        .expectBody()
        .jsonPath("$.accessToken")
        .isNotEmpty()
        .jsonPath("$.refreshToken")
        .isNotEmpty();

    var saved = userRepository.findByEmailAndDeletedAtIsNull(email2).orElseThrow();
    assertTrue(passwordEncoder.matches(NEW_PASSWORD, saved.getPasswordHash()));
  }

  @Test
  void shouldNotChangePassword_whenInvalidOldPassword() {
    var request = new PasswordChangeRequest("n0t-Valid##24", NEW_PASSWORD, NEW_PASSWORD);
    var loginRequest = new LoginRequest(EMAIL, PASSWORD);
    var accessToken = AuthHelper.obtainAccessToken(restTestClient, loginRequest);

    ProfileHelper.performChangePasswordRequest(restTestClient, request, accessToken)
        .expectStatus()
        .isUnauthorized();

    var user = userRepository.findByEmailAndDeletedAtIsNull(EMAIL).orElseThrow();
    assertTrue(passwordEncoder.matches(PASSWORD, user.getPasswordHash()));
  }

  @Test
  void shouldNotChangePassword_whenNewPasswordsAreNotMatching() {
    var request = new PasswordChangeRequest(PASSWORD, NEW_PASSWORD, NEW_PASSWORD + "Diff");
    var loginRequest = new LoginRequest(EMAIL, PASSWORD);
    var accessToken = AuthHelper.obtainAccessToken(restTestClient, loginRequest);

    ProfileHelper.performChangePasswordRequest(restTestClient, request, accessToken)
        .expectStatus()
        .isBadRequest();

    var user = userRepository.findByEmailAndDeletedAtIsNull(EMAIL).orElseThrow();
    assertTrue(passwordEncoder.matches(PASSWORD, user.getPasswordHash()));
  }

  @Test
  void shouldNotChangePassword_whenNewPasswordIsTooWeak() {
    var weakPassword = "weak";
    var request = new PasswordChangeRequest(PASSWORD, weakPassword, weakPassword);
    var loginRequest = new LoginRequest(EMAIL, PASSWORD);
    var accessToken = AuthHelper.obtainAccessToken(restTestClient, loginRequest);

    ProfileHelper.performChangePasswordRequest(restTestClient, request, accessToken)
        .expectStatus()
        .isBadRequest();

    var user = userRepository.findByEmailAndDeletedAtIsNull(EMAIL).orElseThrow();
    assertTrue(passwordEncoder.matches(PASSWORD, user.getPasswordHash()));
  }

  @Test
  void shouldInvalidateOldRefreshToken_whenPasswordChanged() {
    var loginRequest = new LoginRequest(EMAIL, PASSWORD);
    var loginResponse =
        Objects.requireNonNull(
            AuthHelper.performLoginRequest(restTestClient, loginRequest)
                .returnResult(LoginResponse.class)
                .getResponseBody());
    var accessToken = loginResponse.accessToken();
    var oldRefreshToken = Objects.requireNonNull(loginResponse.refreshToken());

    var request = new PasswordChangeRequest(PASSWORD, NEW_PASSWORD, NEW_PASSWORD);
    ProfileHelper.performChangePasswordRequest(restTestClient, request, accessToken)
        .expectStatus()
        .isOk();

    AuthHelper.performRefreshRequest(restTestClient, oldRefreshToken.toString())
        .expectStatus()
        .isUnauthorized();
  }
}
