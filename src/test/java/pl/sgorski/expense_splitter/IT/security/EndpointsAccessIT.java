package pl.sgorski.expense_splitter.IT.security;

import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.client.RestTestClient;
import pl.sgorski.expense_splitter.IT.base.IntegrationTest;
import pl.sgorski.expense_splitter.features.auth.dto.request.LoginRequest;
import pl.sgorski.expense_splitter.features.auth.dto.response.LoginResponse;
import pl.sgorski.expense_splitter.features.user.domain.Role;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.repository.UserRepository;

public class EndpointsAccessIT extends IntegrationTest {

  private final String protectedEndpoint = "/profile";
  private final String protectedAdminEndpoint = "/admin/users";
  private final String notExistingEndpoint = "/not/exsitsing";

  private final String userEmail = "user@example.com";
  private final String userPassword = "Us3rr@2026";
  private final String adminEmail = "admin@example.com";
  private final String adminPassword = "4dminP@ssw0rd";

  @Autowired private UserRepository userRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {
    var user = new User();
    user.setEmail(userEmail);
    user.setPasswordHash(passwordEncoder.encode(userPassword));
    user.setPasswordForChange(false);
    user.setRole(Role.USER);
    userRepository.save(user);

    var admin = new User();
    admin.setEmail(adminEmail);
    admin.setPasswordHash(passwordEncoder.encode(adminPassword));
    admin.setPasswordForChange(false);
    admin.setRole(Role.ADMIN);
    userRepository.save(admin);
  }

  @Test
  void shouldAllowAccessToTheLogin_whenAnonymous() {
    var loginRequest = new LoginRequest(userEmail, userPassword);

    performLoginRequest(loginRequest, null).expectStatus().isOk();
  }

  @Test
  void shouldDenyAccessToProtectedEndpoint_whenAnonymous() {
    performGet(protectedEndpoint).expectStatus().isUnauthorized();
  }

  @Test
  void shouldDenyAccessToProtectedAdminEndpoint_whenAnonymous() {
    performGet(protectedAdminEndpoint).expectStatus().isUnauthorized();
  }

  @Test
  void shouldDenyAccessToAnyOtherEndpoint_whenAnonymous() {
    performGet(notExistingEndpoint).expectStatus().isUnauthorized();
  }

  @Test
  void shouldDenyAccessToTheLogin_whenLoggedAsUser() {
    var loginRequest = new LoginRequest(userEmail, userPassword);
    var token = obtainAccessToken(userEmail, userPassword);

    performLoginRequest(loginRequest, token).expectStatus().isForbidden();
  }

  @Test
  void shouldAllowAccessToProtectedEndpoint_whenLoggedAsUser() {
    var token = obtainAccessToken(userEmail, userPassword);
    performGet(protectedEndpoint, token).expectStatus().isOk();
  }

  @Test
  void shouldDenyAccessToProtectedAdminEndpoint_whenLoggedAsUser() {
    var token = obtainAccessToken(userEmail, userPassword);
    performGet(protectedAdminEndpoint, token).expectStatus().isForbidden();
  }

  @Test
  void shouldDenyAccessToAnyOtherEndpoint_whenLoggedAsUser() {
    var token = obtainAccessToken(userEmail, userPassword);
    performGet(notExistingEndpoint, token).expectStatus().isForbidden();
  }

  @Test
  void shouldDenyAccessToTheLogin_whenLoggedAsAdmin() {
    var loginRequest = new LoginRequest(userEmail, userPassword);
    var token = obtainAccessToken(adminEmail, adminPassword);

    performLoginRequest(loginRequest, token).expectStatus().isForbidden();
  }

  @Test
  void shouldAllowAccessToProtectedEndpoint_whenLoggedAsAdmin() {
    var token = obtainAccessToken(adminEmail, adminPassword);
    performGet(protectedEndpoint, token).expectStatus().isOk();
  }

  @Test
  void shouldAllowAccessToProtectedAdminEndpoint_whenLoggedAsAdmin() {
    var token = obtainAccessToken(adminEmail, adminPassword);
    performGet(protectedAdminEndpoint, token).expectStatus().isOk();
  }

  @Test
  void shouldDenyAccessToAnyOtherEndpoint_whenLoggedAsAdmin() {
    var token = obtainAccessToken(adminEmail, adminPassword);
    performGet(notExistingEndpoint, token).expectStatus().isForbidden();
  }

  private RestTestClient.ResponseSpec performGet(String url) {
    return restTestClient.get().uri(url).exchange();
  }

  private RestTestClient.ResponseSpec performGet(String url, String accessToken) {
    return restTestClient
        .get()
        .uri(url)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        .exchange();
  }

  private RestTestClient.ResponseSpec performLoginRequest(
      LoginRequest request, @Nullable String token) {
    var loginPost = restTestClient.post().uri("/auth/login").body(request);

    if (token != null) {
      loginPost.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }
    return loginPost.exchange();
  }

  private String obtainAccessToken(String username, String password) {
    var loginRequest = new LoginRequest(username, password);
    var response = performLoginRequest(loginRequest, null);
    response.expectStatus().isOk();
    return Objects.requireNonNull(response.returnResult(LoginResponse.class).getResponseBody())
        .accessToken();
  }
}
