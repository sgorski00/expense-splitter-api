package pl.sgorski.expense_splitter.features.auth.local.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import pl.sgorski.expense_splitter.features.auth.dto.response.LoginResponse;
import pl.sgorski.expense_splitter.features.auth.refresh_token.domain.RefreshToken;
import pl.sgorski.expense_splitter.features.auth.refresh_token.service.RefreshTokenCookieResponseHelper;
import pl.sgorski.expense_splitter.features.auth.refresh_token.service.RefreshTokenService;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.security.access_token.AccessTokenService;

@ExtendWith(MockitoExtension.class)
public class TokenResponseEntityCreatorTest {

  @Mock private AccessTokenService accessTokenService;
  @Mock private RefreshTokenService refreshTokenService;
  @Mock private RefreshTokenCookieResponseHelper refreshTokenCookieResponseHelper;
  @InjectMocks private TokenResponseEntityCreator tokenResponseEntityCreator;

  @Test
  void generate_shouldReturnResponseEntityWithAccessAndRefreshToken_whenUserIsValid() {
    var refreshCookie = ResponseCookie.from("test-refresh-cookie").build();
    var user = new User();
    var accessToken = "access-token";
    var refreshToken = UUID.randomUUID();
    var refreshTokenEntity = new RefreshToken();
    refreshTokenEntity.setToken(refreshToken);
    when(accessTokenService.generate(eq(user), anyBoolean())).thenReturn(accessToken);
    when(refreshTokenService.generateRefreshToken(eq(user))).thenReturn(refreshTokenEntity);
    when(refreshTokenService.getExpirationSecond()).thenReturn(1L);
    when(refreshTokenCookieResponseHelper.createRefreshTokenCookie(eq(refreshToken), anyLong()))
        .thenReturn(refreshCookie);
    var expectedBody = new LoginResponse(accessToken, refreshToken, false);

    var response = tokenResponseEntityCreator.generate(user, false);
    var body = response.getBody();

    assertNotNull(response);
    assertEquals(refreshCookie.toString(), response.getHeaders().getFirst(HttpHeaders.SET_COOKIE));
    assertEquals(expectedBody, body);
    assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
  }

  @Test
  void
      generate_shouldReturnResponseEntityWithAccessAndWithoutRefreshToken_whenUserIsValidButTwoFaIsPending() {
    var user = new User();
    var accessToken = "access-token";
    when(accessTokenService.generate(eq(user), anyBoolean())).thenReturn(accessToken);
    var expectedBody = new LoginResponse(accessToken, null, true);

    var response = tokenResponseEntityCreator.generate(user, true);
    var body = response.getBody();

    assertNotNull(response);
    assertNull(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE));
    assertEquals(expectedBody, body);
    assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
  }
}
