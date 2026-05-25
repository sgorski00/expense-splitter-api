package pl.sgorski.expense_splitter.security.oauth2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Claims;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.expense_splitter.security.jwt.JwtProvider;

@ExtendWith(MockitoExtension.class)
public class OAuth2ContextServiceTest {

  @Mock private JwtProvider jwtProvider;

  @InjectMocks private OAuth2ContextService oAuth2ContextService;

  private UUID testUserId;

  @BeforeEach
  void setUp() {
    testUserId = UUID.randomUUID();
  }

  @Test
  void generate_shouldReturnToken_whenCalledWithValidParams() {
    when(jwtProvider.generate(any(String.class), any(), anyMap())).thenReturn("valid.token");

    var token = oAuth2ContextService.generate(testUserId, OAuth2Mode.LOGIN);

    assertNotNull(token);
    assertEquals("valid.token", token);
  }

  @Test
  void generate_shouldPassCorrectUserIdAndMode_toJwtProvider() {
    when(jwtProvider.generate(any(String.class), any(), anyMap())).thenReturn("token");

    oAuth2ContextService.generate(testUserId, OAuth2Mode.LINK);

    var captor = ArgumentCaptor.forClass(java.util.Map.class);
    //noinspection unchecked
    verify(jwtProvider).generate(eq(testUserId.toString()), any(), captor.capture());

    var claims = captor.getValue();
    assertTrue(claims.containsKey("mode"));
    assertEquals(OAuth2Mode.LINK, claims.get("mode"));
  }

  @Test
  void parse_shouldReturnPayload_whenTokenIsValid() {
    var token = "valid.token";
    var claims = mock(Claims.class);
    when(claims.getSubject()).thenReturn(testUserId.toString());
    when(claims.get("mode", String.class)).thenReturn("LOGIN");
    when(jwtProvider.parse(token)).thenReturn(claims);

    var payload = oAuth2ContextService.parse(token);

    assertNotNull(payload);
    assertEquals(testUserId, payload.userId());
    assertEquals(OAuth2Mode.LOGIN, payload.mode());
  }

  @Test
  void parse_shouldThrowException_whenTokenIsInvalid() {
    var token = "invalid.token";
    when(jwtProvider.parse(token)).thenThrow(new RuntimeException("invalid token"));

    Assertions.assertThrows(RuntimeException.class, () -> oAuth2ContextService.parse(token));
  }
}
