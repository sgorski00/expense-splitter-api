package pl.sgorski.expense_splitter.IT.base.helper;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import pl.sgorski.expense_splitter.features.user.dto.request.PasswordChangeRequest;
import pl.sgorski.expense_splitter.features.user.dto.request.PasswordSetRequest;

public final class ProfileHelper {

  private ProfileHelper() {}

  public static RestTestClient.ResponseSpec performSetPasswordRequest(
      RestTestClient restTestClient, PasswordSetRequest request, @Nullable String accessToken) {
    var builder =
        restTestClient
            .put()
            .uri("/profile/password")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request);

    if (accessToken != null) {
      builder.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
    }

    return builder.exchange();
  }

  public static RestTestClient.ResponseSpec performChangePasswordRequest(
      RestTestClient restTestClient, PasswordChangeRequest request, @Nullable String accessToken) {
    var builder =
        restTestClient
            .patch()
            .uri("/profile/password")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request);

    if (accessToken != null) {
      builder.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
    }

    return builder.exchange();
  }
}
