package pl.sgorski.expense_splitter.IT.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.client.RestTestClient;
import pl.sgorski.expense_splitter.IT.base.IntegrationTest;
import pl.sgorski.expense_splitter.features.auth.dto.request.RegisterRequest;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.repository.UserRepository;

public class AuthRegisterIT extends IntegrationTest {

  @Autowired private UserRepository userRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  private final String email = "user@example.com";
  private final String password = "P@ssword123";
  private final String firstName = "John";
  private final String lastName = "Doe";

  @Test
  void register_shouldCreateNewUser_whenRequestIsValid() {
    var request = new RegisterRequest(email, firstName, lastName, password, password);

    performRegisterRequest(request)
        .expectStatus()
        .isCreated()
        .expectBody()
        .jsonPath("$.id")
        .isNotEmpty()
        .jsonPath("$.email")
        .isEqualTo(email)
        .jsonPath("$.role")
        .isEqualTo("USER")
        .jsonPath("$.createdAt")
        .isNotEmpty();
  }

  @Test
  void register_shouldCreateNewUser_whenRequestIsValidWithoutName() {
    var request = new RegisterRequest(email, firstName, lastName, password, password);

    performRegisterRequest(request)
        .expectStatus()
        .isCreated()
        .expectBody()
        .jsonPath("$.id")
        .isNotEmpty()
        .jsonPath("$.email")
        .isEqualTo(email)
        .jsonPath("$.role")
        .isEqualTo("USER")
        .jsonPath("$.createdAt")
        .isNotEmpty();
  }

  @Test
  void register_shouldReturn400_whenPasswordsDoesNotMatch() {
    var request = new RegisterRequest(email, firstName, lastName, password, password + ".");

    performRegisterRequest(request)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.status")
        .isEqualTo(400)
        .jsonPath("$.title")
        .isNotEmpty();
  }

  @Test
  void register_shouldReturn400_whenPasswordIsWeak() {
    var password = "weak";
    var request = new RegisterRequest(email, firstName, lastName, password, password);

    performRegisterRequest(request)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.status")
        .isEqualTo(400)
        .jsonPath("$.title")
        .isNotEmpty();
  }

  @Test
  void register_shouldReturn409_whenUserWithGivenEmailAlreadyExists() {
    var existingUser = new User();
    existingUser.setEmail(email);
    existingUser.setPasswordHash(passwordEncoder.encode(password));
    userRepository.save(existingUser);

    var status = 409;
    var request = new RegisterRequest(email, firstName, lastName, password, password);

    performRegisterRequest(request)
        .expectStatus()
        .isEqualTo(status)
        .expectBody()
        .jsonPath("$.status")
        .isEqualTo(status)
        .jsonPath("$.title")
        .isNotEmpty();
  }

  private RestTestClient.ResponseSpec performRegisterRequest(RegisterRequest request) {
    return restTestClient
        .post()
        .uri("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .exchange();
  }
}
