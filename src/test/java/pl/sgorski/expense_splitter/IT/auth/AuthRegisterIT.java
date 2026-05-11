package pl.sgorski.expense_splitter.IT.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.sgorski.expense_splitter.IT.base.IntegrationTest;
import pl.sgorski.expense_splitter.IT.base.factory.UserTestFactory;
import pl.sgorski.expense_splitter.IT.base.helper.AuthHelper;
import pl.sgorski.expense_splitter.features.auth.dto.request.RegisterRequest;
import pl.sgorski.expense_splitter.features.user.domain.Role;
import pl.sgorski.expense_splitter.features.user.repository.UserRepository;
import pl.sgorski.expense_splitter.security.rate_limit.RateLimitType;

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

    AuthHelper.performRegisterRequest(restTestClient, request)
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
  void register_shouldPersistUserWithDefaultRole() {
    var request = new RegisterRequest(email, firstName, lastName, password, password);

    AuthHelper.performRegisterRequest(restTestClient, request);

    var user = userRepository.findByEmailAndDeletedAtIsNull(email).orElseThrow();
    assertEquals(Role.USER, user.getRole());
  }

  @Test
  void register_shouldHashPasswordInDatabase() {
    var request = new RegisterRequest(email, firstName, lastName, password, password);

    AuthHelper.performRegisterRequest(restTestClient, request);

    var user = userRepository.findByEmailAndDeletedAtIsNull(email).orElseThrow();
    assertTrue(passwordEncoder.matches(password, user.getPassword()));
  }

  @Test
  void register_shouldCreateNewUser_whenRequestIsValidWithoutName() {
    var request = new RegisterRequest(email, null, null, password, password);

    AuthHelper.performRegisterRequest(restTestClient, request)
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
    var request = new RegisterRequest(email, firstName, lastName, password, password + "different");

    AuthHelper.performRegisterRequest(restTestClient, request).expectStatus().isBadRequest();
  }

  @Test
  void register_shouldReturn400_whenPasswordIsWeak() {
    var weakPassword = "weak";
    var request = new RegisterRequest(email, firstName, lastName, weakPassword, weakPassword);

    AuthHelper.performRegisterRequest(restTestClient, request).expectStatus().isBadRequest();
  }

  @Test
  void register_shouldReturn409_whenUserWithGivenEmailAlreadyExists() {
    var existingUser = UserTestFactory.createUser(email, passwordEncoder.encode(password));
    userRepository.save(existingUser);

    var request = new RegisterRequest(email, firstName, lastName, password, password);

    AuthHelper.performRegisterRequest(restTestClient, request).expectStatus().isEqualTo(409);
  }

  @Test
  void register_shouldReturn429_whenRateLimitExceed() {
    var request = new RegisterRequest(email, firstName, lastName, password, password);

    for (int i = 0; i < RateLimitType.AUTH.getLimit(); i++) {
      AuthHelper.performRegisterRequest(restTestClient, request);
    }

    AuthHelper.performRegisterRequest(restTestClient, request).expectStatus().isEqualTo(429);
  }
}
