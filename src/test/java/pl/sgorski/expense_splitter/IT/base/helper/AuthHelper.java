package pl.sgorski.expense_splitter.IT.base.helper;

import java.util.Objects;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import pl.sgorski.expense_splitter.features.auth.dto.request.*;
import pl.sgorski.expense_splitter.features.auth.dto.response.LoginResponse;

public final class AuthHelper {

  private AuthHelper() {}

  public static RestTestClient.ResponseSpec performLoginRequest(
      RestTestClient restTestClient, LoginRequest loginRequest) {
    return restTestClient
        .post()
        .uri("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .body(loginRequest)
        .exchange();
  }

  public static RestTestClient.ResponseSpec performLogoutRequest(
      RestTestClient restTestClient, String refreshToken) {
    return restTestClient
        .post()
        .uri("/auth/logout")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + refreshToken)
        .exchange();
  }

  public static RestTestClient.ResponseSpec performLogoutRequestWithCookie(
      RestTestClient restTestClient, String refreshToken) {
    return restTestClient
        .post()
        .uri("/auth/logout")
        .cookie("refreshToken", refreshToken)
        .exchange();
  }

  public static RestTestClient.ResponseSpec performRefreshRequest(
      RestTestClient restTestClient, String refreshToken) {
    return restTestClient
        .post()
        .uri("/auth/refresh")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + refreshToken)
        .exchange();
  }

  public static RestTestClient.ResponseSpec performTwoFaVerificationRequest(
      RestTestClient restTestClient, String accessToken, GoogleAuthenticatorRequest twoFaRequest) {
    var builder =
        restTestClient
            .post()
            .uri("/auth/2fa/verify")
            .contentType(MediaType.APPLICATION_JSON)
            .body(twoFaRequest);

    if (accessToken != null) {
      builder = builder.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
    }

    return builder.exchange();
  }

  public static RestTestClient.ResponseSpec performPasswordResetRequest(
      RestTestClient restTestClient, PasswordResetRequest request) {
    return restTestClient
        .post()
        .uri("/auth/reset-password")
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .exchange();
  }

  public static RestTestClient.ResponseSpec performConfirmPasswordResetRequest(
      RestTestClient restTestClient, ConfirmPasswordResetRequest request) {
    return restTestClient
        .post()
        .uri("/auth/reset-password/confirm")
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .exchange();
  }

  public static RestTestClient.ResponseSpec performRegisterRequest(
      RestTestClient restTestClient, RegisterRequest request) {
    return restTestClient
        .post()
        .uri("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .exchange();
  }

  public static String obtainAccessToken(RestTestClient restTestClient, LoginRequest loginRequest) {
    var response =
        performLoginRequest(restTestClient, loginRequest)
            .expectStatus()
            .isOk()
            .returnResult(LoginResponse.class)
            .getResponseBody();
    return Objects.requireNonNull(response).accessToken();
  }

  public static UUID obtainRefreshToken(RestTestClient restTestClient, LoginRequest loginRequest) {
    var response =
        performLoginRequest(restTestClient, loginRequest)
            .expectStatus()
            .isOk()
            .returnResult(LoginResponse.class)
            .getResponseBody();
    return Objects.requireNonNull(response).refreshToken();
  }
}
