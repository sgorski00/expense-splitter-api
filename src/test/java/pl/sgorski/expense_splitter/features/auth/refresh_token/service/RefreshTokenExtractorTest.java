package pl.sgorski.expense_splitter.features.auth.refresh_token.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.sgorski.expense_splitter.exceptions.RefreshTokenValidationException;
import pl.sgorski.expense_splitter.utils.AuthorizationTokenUtils;

public class RefreshTokenExtractorTest {

  private RefreshTokenExtractor refreshTokenExtractor;
  private HttpServletRequest request;

  @BeforeEach
  void setUp() {
    refreshTokenExtractor = new RefreshTokenExtractor();
    request = mock(HttpServletRequest.class);
  }

  @Test
  void extract_shouldExtractTokenFromCookie() {
    var cookieUuid = UUID.randomUUID();

    var tokenUuid = refreshTokenExtractor.extract(cookieUuid, request);

    assertEquals(cookieUuid, tokenUuid);
  }

  @Test
  void extract_shouldExtractTokenFromHeader_notFoundInCookieAndHeaderValid() {
    var headerUuid = UUID.randomUUID();
    var header = "Bearer " + headerUuid;
    when(request.getHeader(AuthorizationTokenUtils.AUTHORIZATION_HEADER)).thenReturn(header);

    var tokenUuid = refreshTokenExtractor.extract(null, request);

    assertEquals(headerUuid, tokenUuid);
  }

  @Test
  void extract_shouldThrow_notFoundInCookieAndHeaderValueInvalid() {
    var header = "Bearer not-valid-uuid";
    when(request.getHeader(AuthorizationTokenUtils.AUTHORIZATION_HEADER)).thenReturn(header);

    assertThrows(
        RefreshTokenValidationException.class, () -> refreshTokenExtractor.extract(null, request));
  }

  @Test
  void extract_shouldThrow_notFoundInCookieAndHeaderIsNull() {
    when(request.getHeader(AuthorizationTokenUtils.AUTHORIZATION_HEADER)).thenReturn(null);

    assertThrows(
        RefreshTokenValidationException.class, () -> refreshTokenExtractor.extract(null, request));
  }

  @Test
  void extract_shouldThrow_notFoundInCookieAndHeaderDontStartWithBearer() {
    var header = "NOT-A-BEARER " + UUID.randomUUID();
    when(request.getHeader(AuthorizationTokenUtils.AUTHORIZATION_HEADER)).thenReturn(header);

    assertThrows(
        RefreshTokenValidationException.class, () -> refreshTokenExtractor.extract(null, request));
  }
}
