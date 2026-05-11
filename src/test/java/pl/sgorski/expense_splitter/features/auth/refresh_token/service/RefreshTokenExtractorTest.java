package pl.sgorski.expense_splitter.features.auth.refresh_token.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.sgorski.expense_splitter.exceptions.authentication.RefreshTokenValidationException;
import pl.sgorski.expense_splitter.utils.AuthorizationTokenUtils;

class RefreshTokenExtractorTest {

  private RefreshTokenExtractor extractor;
  private HttpServletRequest request;

  @BeforeEach
  void setUp() {
    extractor = new RefreshTokenExtractor();
    request = mock(HttpServletRequest.class);
  }

  @Test
  void requireExtract_shouldReturnToken_fromCookie() {
    var cookie = UUID.randomUUID();

    var result = extractor.requireExtract(cookie, request);

    assertEquals(cookie, result);
  }

  @Test
  void requireExtract_shouldReturnToken_fromHeader() {
    var uuid = UUID.randomUUID();
    when(request.getHeader(AuthorizationTokenUtils.AUTHORIZATION_HEADER))
        .thenReturn("Bearer " + uuid);

    var result = extractor.requireExtract(null, request);

    assertEquals(uuid, result);
  }

  @Test
  void requireExtract_shouldThrow_whenNoTokenProvided() {
    when(request.getHeader(AuthorizationTokenUtils.AUTHORIZATION_HEADER)).thenReturn(null);

    assertThrows(
        RefreshTokenValidationException.class, () -> extractor.requireExtract(null, request));
  }

  @Test
  void requireExtract_shouldThrow_whenHeaderIsInvalidUuid() {
    when(request.getHeader(AuthorizationTokenUtils.AUTHORIZATION_HEADER))
        .thenReturn("Bearer not-uuid");

    assertThrows(
        RefreshTokenValidationException.class, () -> extractor.requireExtract(null, request));
  }

  @Test
  void requireExtract_shouldThrow_whenHeaderDoesNotStartWithBearer() {
    when(request.getHeader(AuthorizationTokenUtils.AUTHORIZATION_HEADER))
        .thenReturn("INVALID " + UUID.randomUUID());

    assertThrows(
        RefreshTokenValidationException.class, () -> extractor.requireExtract(null, request));
  }

  @Test
  void extract_shouldReturnOptionalFromCookie() {
    var cookie = UUID.randomUUID();

    var result = extractor.extract(cookie, request);

    assertEquals(Optional.of(cookie), result);
  }

  @Test
  void extract_shouldReturnOptionalFromHeader() {
    var uuid = UUID.randomUUID();
    when(request.getHeader(AuthorizationTokenUtils.AUTHORIZATION_HEADER))
        .thenReturn("Bearer " + uuid);

    var result = extractor.extract(null, request);

    assertEquals(Optional.of(uuid), result);
  }

  @Test
  void extract_shouldReturnEmpty_whenNoToken() {
    when(request.getHeader(AuthorizationTokenUtils.AUTHORIZATION_HEADER)).thenReturn(null);

    var result = extractor.extract(null, request);

    assertTrue(result.isEmpty());
  }

  @Test
  void extract_shouldReturnEmpty_whenInvalidUuid() {
    when(request.getHeader(AuthorizationTokenUtils.AUTHORIZATION_HEADER))
        .thenReturn("Bearer not-uuid");

    var result = extractor.extract(null, request);

    assertTrue(result.isEmpty());
  }

  @Test
  void extract_shouldReturnEmpty_whenHeaderDoesNotStartWithBearer() {
    when(request.getHeader(AuthorizationTokenUtils.AUTHORIZATION_HEADER))
        .thenReturn("INVALID " + UUID.randomUUID());

    var result = extractor.extract(null, request);

    assertTrue(result.isEmpty());
  }
}
