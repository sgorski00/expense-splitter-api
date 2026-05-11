package pl.sgorski.expense_splitter.IT.security;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.client.RestTestClient;
import pl.sgorski.expense_splitter.IT.base.IntegrationTest;
import pl.sgorski.expense_splitter.IT.base.factory.UserTestFactory;
import pl.sgorski.expense_splitter.IT.base.helper.AuthHelper;
import pl.sgorski.expense_splitter.features.auth.dto.request.LoginRequest;
import pl.sgorski.expense_splitter.features.user.domain.Role;
import pl.sgorski.expense_splitter.features.user.repository.UserRepository;

public class EndpointsAccessIT extends IntegrationTest {

  private static final String PROTECTED_ENDPOINT = "/profile";
  private static final String PROTECTED_ADMIN_ENDPOINT = "/admin/users";
  private static final String NOT_EXISTING_ENDPOINT = "/not/exsitsing";

  private static final String USER_EMAIL = "user@example.com";
  private static final String USER_PASSWORD = "Us3rr@2026";
  private static final String ADMIN_EMAIL = "admin@example.com";
  private static final String ADMIN_PASSWORD = "4dminP@ssw0rd";

  @Autowired private UserRepository userRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {
    var user = UserTestFactory.createUser(USER_EMAIL, passwordEncoder.encode(USER_PASSWORD));
    user.setRole(Role.USER);
    userRepository.save(user);

    var admin = UserTestFactory.createUser(ADMIN_EMAIL, passwordEncoder.encode(ADMIN_PASSWORD));
    admin.setRole(Role.ADMIN);
    userRepository.save(admin);
  }

  @Test
  void shouldAllowAccessToTheLogin_whenAnonymous() {
    var loginRequest = new LoginRequest(USER_EMAIL, USER_PASSWORD);

    performLoginRequest(loginRequest, null).expectStatus().isOk();
  }

  @Test
  void shouldDenyAccessToProtectedEndpoint_whenAnonymous() {
    performGet(PROTECTED_ENDPOINT).expectStatus().isUnauthorized();
  }

  @Test
  void shouldDenyAccessToProtectedAdminEndpoint_whenAnonymous() {
    performGet(PROTECTED_ADMIN_ENDPOINT).expectStatus().isUnauthorized();
  }

  @Test
  void shouldDenyAccessToAnyOtherEndpoint_whenAnonymous() {
    performGet(NOT_EXISTING_ENDPOINT).expectStatus().isUnauthorized();
  }

  @Test
  void shouldDenyAccessToTheLogin_whenLoggedAsUser() {
    var loginRequest = new LoginRequest(USER_EMAIL, USER_PASSWORD);
    var token =
        AuthHelper.obtainAccessToken(restTestClient, new LoginRequest(USER_EMAIL, USER_PASSWORD));

    performLoginRequest(loginRequest, token).expectStatus().isForbidden();
  }

  @Test
  void shouldAllowAccessToProtectedEndpoint_whenLoggedAsUser() {
    var token =
        AuthHelper.obtainAccessToken(restTestClient, new LoginRequest(USER_EMAIL, USER_PASSWORD));
    performGet(PROTECTED_ENDPOINT, token).expectStatus().isOk();
  }

  @Test
  void shouldDenyAccessToProtectedAdminEndpoint_whenLoggedAsUser() {
    var token =
        AuthHelper.obtainAccessToken(restTestClient, new LoginRequest(USER_EMAIL, USER_PASSWORD));
    performGet(PROTECTED_ADMIN_ENDPOINT, token).expectStatus().isForbidden();
  }

  @Test
  void shouldDenyAccessToAnyOtherEndpoint_whenLoggedAsUser() {
    var token =
        AuthHelper.obtainAccessToken(restTestClient, new LoginRequest(USER_EMAIL, USER_PASSWORD));
    performGet(NOT_EXISTING_ENDPOINT, token).expectStatus().isForbidden();
  }

  @Test
  void shouldDenyAccessToTheLogin_whenLoggedAsAdmin() {
    var loginRequest = new LoginRequest(USER_EMAIL, USER_PASSWORD);
    var token =
        AuthHelper.obtainAccessToken(restTestClient, new LoginRequest(ADMIN_EMAIL, ADMIN_PASSWORD));

    performLoginRequest(loginRequest, token).expectStatus().isForbidden();
  }

  @Test
  void shouldAllowAccessToProtectedEndpoint_whenLoggedAsAdmin() {
    var token =
        AuthHelper.obtainAccessToken(restTestClient, new LoginRequest(ADMIN_EMAIL, ADMIN_PASSWORD));
    performGet(PROTECTED_ENDPOINT, token).expectStatus().isOk();
  }

  @Test
  void shouldAllowAccessToProtectedAdminEndpoint_whenLoggedAsAdmin() {
    var token =
        AuthHelper.obtainAccessToken(restTestClient, new LoginRequest(ADMIN_EMAIL, ADMIN_PASSWORD));
    performGet(PROTECTED_ADMIN_ENDPOINT, token).expectStatus().isOk();
  }

  @Test
  void shouldDenyAccessToAnyOtherEndpoint_whenLoggedAsAdmin() {
    var token =
        AuthHelper.obtainAccessToken(restTestClient, new LoginRequest(ADMIN_EMAIL, ADMIN_PASSWORD));
    performGet(NOT_EXISTING_ENDPOINT, token).expectStatus().isForbidden();
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
    var loginPost =
        restTestClient
            .post()
            .uri("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request);

    if (token != null) {
      loginPost = loginPost.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }
    return loginPost.exchange();
  }
}
