package pl.sgorski.expense_splitter.IT.auth;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.sgorski.expense_splitter.IT.base.IntegrationTest;
import pl.sgorski.expense_splitter.IT.base.factory.UserTestFactory;
import pl.sgorski.expense_splitter.IT.base.helper.AuthHelper;
import pl.sgorski.expense_splitter.features.auth.dto.request.LoginRequest;
import pl.sgorski.expense_splitter.features.auth.refresh_token.repository.RefreshTokenRepository;
import pl.sgorski.expense_splitter.features.user.repository.UserRepository;

public class AuthLogoutIT extends IntegrationTest {

  @Autowired private RefreshTokenRepository refreshTokenRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  private static final String EMAIL = "user@example.com";
  private static final String RAW_PASSWORD = "P@ssword123";

  @BeforeEach
  void setUp() {
    var user = UserTestFactory.createUser(EMAIL, passwordEncoder.encode(RAW_PASSWORD));
    userRepository.save(user);
  }

  @Test
  void logout_shouldRemoveRefreshTokenAndSetClearCookie_whenUserIsLoggedIn() {
    var loginRequest = new LoginRequest(EMAIL, RAW_PASSWORD);
    var refreshToken = AuthHelper.obtainRefreshToken(restTestClient, loginRequest);

    AuthHelper.performLogoutRequest(restTestClient, refreshToken.toString())
        .expectStatus()
        .isNoContent()
        .expectHeader()
        .exists(HttpHeaders.SET_COOKIE);

    var token = refreshTokenRepository.findByToken(refreshToken).orElseThrow();
    assertTrue(token.isRevoked());
  }

  @Test
  void logout_shouldWorkWithCookieToken() {
    var loginRequest = new LoginRequest(EMAIL, RAW_PASSWORD);

    var refreshToken = AuthHelper.obtainRefreshToken(restTestClient, loginRequest);

    AuthHelper.performLogoutRequestWithCookie(restTestClient, refreshToken.toString())
        .expectStatus()
        .isNoContent();

    var token = refreshTokenRepository.findByToken(refreshToken).orElseThrow();
    assertTrue(token.isRevoked());
  }

  @Test
  void logout_shouldBeIdempotent_whenRefreshTokenNotValid() {
    var before = refreshTokenRepository.count();

    AuthHelper.performLogoutRequest(restTestClient, "not-valid-token").expectStatus().isNoContent();

    assertEquals(before, refreshTokenRepository.count());
  }

  @Test
  void logout_shouldReturn405_whenTryingToLogoutWithGetMethod() {
    var loginRequest = new LoginRequest(EMAIL, RAW_PASSWORD);
    var accessToken = AuthHelper.obtainAccessToken(restTestClient, loginRequest);

    restTestClient
        .get()
        .uri("/auth/logout")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        .exchange()
        .expectStatus()
        .isEqualTo(405);
  }

  @Test
  void logout_shouldPreventRefreshAfterLogout() {
    var loginRequest = new LoginRequest(EMAIL, RAW_PASSWORD);

    var refreshToken = AuthHelper.obtainRefreshToken(restTestClient, loginRequest).toString();

    AuthHelper.performLogoutRequest(restTestClient, refreshToken).expectStatus().isNoContent();

    AuthHelper.performRefreshRequest(restTestClient, refreshToken).expectStatus().isUnauthorized();
  }

  @Test
  void logout_shouldOnlyRevokeOneSession() {
    var loginRequest = new LoginRequest(EMAIL, RAW_PASSWORD);

    var token1 = AuthHelper.obtainRefreshToken(restTestClient, loginRequest);
    var token2 = AuthHelper.obtainRefreshToken(restTestClient, loginRequest);

    AuthHelper.performLogoutRequest(restTestClient, token1.toString()).expectStatus().isNoContent();

    assertTrue(refreshTokenRepository.findByToken(token1).orElseThrow().isRevoked());
    assertFalse(refreshTokenRepository.findByToken(token2).orElseThrow().isRevoked());
  }
}
