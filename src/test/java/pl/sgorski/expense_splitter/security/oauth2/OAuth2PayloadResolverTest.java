package pl.sgorski.expense_splitter.security.oauth2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OAuth2PayloadResolverTest {

  @Mock private OAuth2ContextCookieService cookieService;

  @Mock private OAuth2ContextService contextService;

  @InjectMocks private OAuth2PayloadResolver resolver;

  @Test
  void consume_shouldReturnPayloadAndClearCookie_whenCookieExistsAndIsValid() {
    var token = "valid.token";
    var expectedPayload = new OAuth2ContextPayload(UUID.randomUUID(), OAuth2Mode.LOGIN);

    when(cookieService.read()).thenReturn(Optional.of(token));
    when(contextService.parse(token)).thenReturn(expectedPayload);

    var result = resolver.consume();

    assertTrue(result.isPresent());
    assertEquals(expectedPayload, result.get());
    verify(cookieService).clear();
  }

  @Test
  void consume_shouldReturnEmptyAndClearCookie_whenCookieDoesNotExist() {
    when(cookieService.read()).thenReturn(Optional.empty());

    var result = resolver.consume();

    assertTrue(result.isEmpty());
    verify(cookieService).clear();
  }

  @Test
  void consume_shouldThrowException_whenTokenIsInvalid() {
    var token = "invalid.token";

    when(cookieService.read()).thenReturn(Optional.of(token));
    when(contextService.parse(token)).thenThrow(new RuntimeException("Invalid token format"));

    assertThrows(RuntimeException.class, () -> resolver.consume());
  }
}
