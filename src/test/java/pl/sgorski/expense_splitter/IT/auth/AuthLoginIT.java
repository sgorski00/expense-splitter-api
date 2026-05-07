package pl.sgorski.expense_splitter.IT.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.sgorski.expense_splitter.IT.base.IntegrationTest;
import pl.sgorski.expense_splitter.features.auth.dto.request.LoginRequest;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.repository.UserRepository;

public class AuthLoginIT extends IntegrationTest {

  @Autowired private UserRepository userRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  private final String email = "user@example.com";
  private final String rawPassword = "P@ssword123";

  @BeforeEach
  void setUpUser() {
    userRepository.deleteAll();

    var user = new User();
    user.setEmail(email);
    user.setPasswordHash(passwordEncoder.encode(rawPassword));
    user.setPasswordForChange(false);
    userRepository.save(user);
  }

  @Test
  void login_shouldReturnAccessTokenAndRefreshToken_whenCredentialsAreValid() {
    var request = new LoginRequest(email, rawPassword);

    restTestClient
        .post()
        .uri("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .exchange()
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
}
