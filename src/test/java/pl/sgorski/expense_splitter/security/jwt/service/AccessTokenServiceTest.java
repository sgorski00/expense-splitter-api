package pl.sgorski.expense_splitter.security.jwt.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.jsonwebtoken.Claims;
import java.util.Collection;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.expense_splitter.features.user.domain.Role;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.security.jwt.config.JwtProperties;

@ExtendWith(MockitoExtension.class)
public class AccessTokenServiceTest {

  @Mock private JwtProvider jwtProvider;
  @Mock private JwtProperties jwtProperties;
  @InjectMocks private AccessTokenService accessTokenService;

  private User testUser;
  private final long expirationTimeInMs = 3600000L;

  @BeforeEach
  void setUp() {

    testUser = new User();
    testUser.setId(UUID.randomUUID());
    testUser.setEmail("testuser@example.com");
    testUser.setPasswordForChange(false);
    testUser.setRole(Role.USER);
  }

  @Test
  void generate_shouldGenerateToken_withCorrectUserData() {
    when(jwtProperties.expirationTimeInMs()).thenReturn(expirationTimeInMs);
    when(jwtProvider.generate(anyString(), any(), anyMap())).thenReturn("valid.jwt.token");

    var token = accessTokenService.generate(testUser, false);

    assertNotNull(token);
    assertEquals("valid.jwt.token", token);
  }

  @Test
  void generate_shouldPassCorrectSubject_toJwtProvider() {
    when(jwtProperties.expirationTimeInMs()).thenReturn(expirationTimeInMs);
    when(jwtProvider.generate(anyString(), any(), anyMap())).thenReturn("valid.jwt.token");

    accessTokenService.generate(testUser, false);

    verify(jwtProvider).generate(eq(String.valueOf(testUser.getId())), any(), anyMap());
  }

  @Test
  void generate_shouldIncludeEmailClaim_inToken() {
    when(jwtProperties.expirationTimeInMs()).thenReturn(expirationTimeInMs);
    var captor = ArgumentCaptor.forClass(java.util.Map.class);
    //noinspection unchecked
    when(jwtProvider.generate(anyString(), any(), captor.capture())).thenReturn("token");

    accessTokenService.generate(testUser, false);

    var claims = captor.getValue();
    assertEquals(testUser.getEmail(), claims.get("email"));
  }

  @Test
  void generate_shouldIncludeRolesClaim_inToken() {
    when(jwtProperties.expirationTimeInMs()).thenReturn(expirationTimeInMs);
    var captor = ArgumentCaptor.forClass(java.util.Map.class);
    //noinspection unchecked
    when(jwtProvider.generate(anyString(), any(), captor.capture())).thenReturn("token");
    testUser.setRole(Role.USER);

    accessTokenService.generate(testUser, false);

    var claims = captor.getValue();
    //noinspection unchecked
    var roles = (Collection<String>) claims.get("roles");
    assertNotNull(roles);
    assertFalse(roles.isEmpty());
  }

  @Test
  void generate_shouldIncludePasswordForChangeClaim_whenTrue() {
    when(jwtProperties.expirationTimeInMs()).thenReturn(expirationTimeInMs);
    var captor = ArgumentCaptor.forClass(java.util.Map.class);
    //noinspection unchecked
    when(jwtProvider.generate(anyString(), any(), captor.capture())).thenReturn("token");
    testUser.setPasswordForChange(true);

    accessTokenService.generate(testUser, false);

    var claims = captor.getValue();
    assertEquals(true, claims.get("passwordForChange"));
  }

  @Test
  void generate_shouldIncludePasswordForChangeClaim_whenFalse() {
    when(jwtProperties.expirationTimeInMs()).thenReturn(expirationTimeInMs);
    var captor = ArgumentCaptor.forClass(java.util.Map.class);
    //noinspection unchecked
    when(jwtProvider.generate(anyString(), any(), captor.capture())).thenReturn("token");
    testUser.setPasswordForChange(false);

    accessTokenService.generate(testUser, false);

    var claims = captor.getValue();
    assertEquals(false, claims.get("passwordForChange"));
  }

  @Test
  void generate_shouldIncludeTwoFactorRequiredClaim_whenTrue() {
    when(jwtProperties.expirationTimeInMs()).thenReturn(expirationTimeInMs);
    var captor = ArgumentCaptor.forClass(java.util.Map.class);
    //noinspection unchecked
    when(jwtProvider.generate(anyString(), any(), captor.capture())).thenReturn("token");

    accessTokenService.generate(testUser, true);

    var claims = captor.getValue();
    assertEquals(true, claims.get("twoFactorRequired"));
  }

  @Test
  void generate_shouldIncludeTwoFactorRequiredClaim_whenFalse() {
    when(jwtProperties.expirationTimeInMs()).thenReturn(expirationTimeInMs);
    var captor = ArgumentCaptor.forClass(java.util.Map.class);
    //noinspection unchecked
    when(jwtProvider.generate(anyString(), any(), captor.capture())).thenReturn("token");

    accessTokenService.generate(testUser, false);

    var claims = captor.getValue();
    assertEquals(false, claims.get("twoFactorRequired"));
  }

  @Test
  void parse_shouldReturnAccessTokenPayload_withCorrectUserId() {
    var token = "valid.jwt.token";
    var claims = mock(Claims.class);
    when(claims.getSubject()).thenReturn(String.valueOf(testUser.getId()));
    when(claims.get("email", String.class)).thenReturn(testUser.getEmail());
    when(claims.get("passwordForChange", Boolean.class)).thenReturn(false);
    when(claims.get("twoFactorRequired", Boolean.class)).thenReturn(false);
    when(jwtProvider.parse(token)).thenReturn(claims);

    var payload = accessTokenService.parse(token);

    assertEquals(testUser.getId(), payload.userId());
  }

  @Test
  void parse_shouldReturnAccessTokenPayload_withCorrectEmail() {
    var token = "valid.jwt.token";
    var claims = mock(Claims.class);
    when(claims.getSubject()).thenReturn(String.valueOf(testUser.getId()));
    when(claims.get("email", String.class)).thenReturn(testUser.getEmail());
    when(claims.get("passwordForChange", Boolean.class)).thenReturn(false);
    when(claims.get("twoFactorRequired", Boolean.class)).thenReturn(false);
    when(jwtProvider.parse(token)).thenReturn(claims);

    var payload = accessTokenService.parse(token);

    assertEquals(testUser.getEmail(), payload.email());
  }

  @Test
  void parse_shouldReturnAccessTokenPayload_withPasswordForChangeTrue() {
    var token = "valid.jwt.token";
    var claims = mock(Claims.class);
    when(claims.getSubject()).thenReturn(String.valueOf(testUser.getId()));
    when(claims.get("email", String.class)).thenReturn(testUser.getEmail());
    when(claims.get("passwordForChange", Boolean.class)).thenReturn(true);
    when(claims.get("twoFactorRequired", Boolean.class)).thenReturn(false);
    when(jwtProvider.parse(token)).thenReturn(claims);

    var payload = accessTokenService.parse(token);

    assertTrue(payload.passwordForChange());
  }

  @Test
  void parse_shouldReturnAccessTokenPayload_withPasswordForChangeFalse() {
    var token = "valid.jwt.token";
    var claims = mock(Claims.class);
    when(claims.getSubject()).thenReturn(String.valueOf(testUser.getId()));
    when(claims.get("email", String.class)).thenReturn(testUser.getEmail());
    when(claims.get("passwordForChange", Boolean.class)).thenReturn(false);
    when(claims.get("twoFactorRequired", Boolean.class)).thenReturn(false);
    when(jwtProvider.parse(token)).thenReturn(claims);

    var payload = accessTokenService.parse(token);

    assertFalse(payload.passwordForChange());
  }

  @Test
  void parse_shouldReturnAccessTokenPayload_withTwoFactorRequiredTrue() {
    var token = "valid.jwt.token";
    var claims = mock(Claims.class);
    when(claims.getSubject()).thenReturn(String.valueOf(testUser.getId()));
    when(claims.get("email", String.class)).thenReturn(testUser.getEmail());
    when(claims.get("passwordForChange", Boolean.class)).thenReturn(false);
    when(claims.get("twoFactorRequired", Boolean.class)).thenReturn(true);
    when(jwtProvider.parse(token)).thenReturn(claims);

    var payload = accessTokenService.parse(token);

    assertTrue(payload.twoFactorRequired());
  }

  @Test
  void parse_shouldReturnAccessTokenPayload_withTwoFactorRequiredFalse() {
    var token = "valid.jwt.token";
    var claims = mock(Claims.class);
    when(claims.getSubject()).thenReturn(String.valueOf(testUser.getId()));
    when(claims.get("email", String.class)).thenReturn(testUser.getEmail());
    when(claims.get("passwordForChange", Boolean.class)).thenReturn(false);
    when(claims.get("twoFactorRequired", Boolean.class)).thenReturn(false);
    when(jwtProvider.parse(token)).thenReturn(claims);

    var payload = accessTokenService.parse(token);

    assertFalse(payload.twoFactorRequired());
  }

  @Test
  void parse_shouldThrowException_whenEmailClaimIsMissing() {
    var token = "valid.jwt.token";
    var claims = mock(Claims.class);
    when(claims.getSubject()).thenReturn(String.valueOf(testUser.getId()));
    when(claims.get("email", String.class)).thenReturn(null);
    when(jwtProvider.parse(token)).thenReturn(claims);

    assertThrows(NullPointerException.class, () -> accessTokenService.parse(token));
  }

  @Test
  void parse_shouldThrowException_whenPasswordForChangeClaimIsMissing() {
    var token = "valid.jwt.token";
    var claims = mock(Claims.class);
    when(claims.getSubject()).thenReturn(String.valueOf(testUser.getId()));
    when(claims.get("email", String.class)).thenReturn(testUser.getEmail());
    when(claims.get("passwordForChange", Boolean.class)).thenReturn(null);
    when(jwtProvider.parse(token)).thenReturn(claims);

    assertThrows(NullPointerException.class, () -> accessTokenService.parse(token));
  }

  @Test
  void parse_shouldThrowException_whenTwoFactorRequiredClaimIsMissing() {
    var token = "valid.jwt.token";
    var claims = mock(Claims.class);
    when(claims.getSubject()).thenReturn(String.valueOf(testUser.getId()));
    when(claims.get("email", String.class)).thenReturn(testUser.getEmail());
    when(claims.get("passwordForChange", Boolean.class)).thenReturn(false);
    when(claims.get("twoFactorRequired", Boolean.class)).thenReturn(null);
    when(jwtProvider.parse(token)).thenReturn(claims);

    assertThrows(NullPointerException.class, () -> accessTokenService.parse(token));
  }

  @Test
  void parse_shouldThrowException_whenTokenIsInvalid() {
    var invalidToken = "invalid.token.here";
    when(jwtProvider.parse(invalidToken)).thenThrow(new RuntimeException("Invalid token"));

    assertThrows(RuntimeException.class, () -> accessTokenService.parse(invalidToken));
  }
}
