package pl.sgorski.expense_splitter.security.jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.expense_splitter.features.user.domain.Role;
import pl.sgorski.expense_splitter.features.user.domain.User;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTests {

  private final JwtProperties jwtProperties = mock(JwtProperties.class);
  private SecretKey secretKey;
  private JwtService jwtService;

  private User testUser;
  private final long expirationTimeInMs = 3600000L;

  @BeforeEach
  void setUp() {
    var secretKeyString = "MyVeryLongSecretKeyThatIsAtLeast32BytesLongForHS256Algorithm";
    secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    jwtService = new JwtService(jwtProperties, secretKey);
    when(jwtProperties.secretKey()).thenReturn(secretKeyString);
    when(jwtProperties.expirationTimeInMs()).thenReturn(expirationTimeInMs);

    testUser = new User();
    testUser.setId(UUID.randomUUID());
    testUser.setEmail("testuser@example.com");
    testUser.setPasswordForChange(false);
    testUser.setRole(Role.USER);
  }

  @Test
  void generateAccessToken_shouldGenerateValidToken_withCorrectUserData() {
    var token = jwtService.generateAccessToken(testUser);

    assertNotNull(token);
    assertFalse(token.isBlank());
    assertTrue(token.contains("."));
  }

  @Test
  void generateAccessToken_shouldEncodeUserIdAsSubject_correctToken() {
    var token = jwtService.generateAccessToken(testUser);
    var claims = parseToken(token);

    assertEquals(String.valueOf(testUser.getId()), claims.getSubject());
  }

  @Test
  void generateAccessToken_shouldIncludeEmailClaim_correctToken() {
    var token = jwtService.generateAccessToken(testUser);
    var claims = parseToken(token);

    assertEquals(testUser.getEmail(), claims.get("email", String.class));
  }

  @Test
  void generateAccessToken_shouldIncludeRolesClaim_withUserAuthorities() {
    testUser.setRole(Role.USER);

    var token = jwtService.generateAccessToken(testUser);
    var claims = parseToken(token);

    @SuppressWarnings("unchecked")
    var roles = (Collection<String>) claims.get("roles");
    assertNotNull(roles);
    assertFalse(roles.isEmpty());
  }

  @Test
  void generateAccessToken_shouldIncludePasswordForChangeClaim_whenTrue() {
    testUser.setPasswordForChange(true);

    var token = jwtService.generateAccessToken(testUser);
    var claims = parseToken(token);

    assertEquals(true, claims.get("passwordForChange", Boolean.class));
  }

  @Test
  void generateAccessToken_shouldIncludePasswordForChangeClaim_whenFalse() {
    testUser.setPasswordForChange(false);

    var token = jwtService.generateAccessToken(testUser);
    var claims = parseToken(token);

    assertEquals(false, claims.get("passwordForChange", Boolean.class));
  }

  @Test
  void generateAccessToken_shouldSetExpirationTime_correctToken() {
    var beforeToken = Instant.now();
    var token = jwtService.generateAccessToken(testUser);
    var afterToken = Instant.now();
    var claims = parseToken(token);

    var expiration = claims.getExpiration().toInstant();

    assertTrue(
        expiration.isAfter(beforeToken)
            && expiration.isBefore(afterToken.plusMillis(expirationTimeInMs)));
  }

  @Test
  void isTokenInvalid_shouldReturnFalse_whenTokenIsValid() {
    var token = jwtService.generateAccessToken(testUser);

    var result = jwtService.isTokenInvalid(token);

    assertFalse(result);
  }

  @Test
  void isTokenInvalid_shouldReturnTrue_whenTokenIsExpired() {
    when(jwtProperties.expirationTimeInMs()).thenReturn(-1000L);
    var token = jwtService.generateAccessToken(testUser);

    var result = jwtService.isTokenInvalid(token);

    assertTrue(result);
  }

  @Test
  void isTokenInvalid_shouldReturnTrue_whenTokenIsInvalid() {
    var invalidToken = "invalid.token.here";

    var result = jwtService.isTokenInvalid(invalidToken);

    assertTrue(result);
  }

  @Test
  void getEmailFromToken_shouldReturnCorrectEmail_validToken() {
    var token = jwtService.generateAccessToken(testUser);

    var email = jwtService.getEmailFromToken(token);

    assertEquals(testUser.getEmail(), email);
  }

  @Test
  void getEmailFromToken_shouldThrowException_whenTokenIsInvalid() {
    var invalidToken = "invalid.token.here";

    assertThrows(Exception.class, () -> jwtService.getEmailFromToken(invalidToken));
  }

  @Test
  void getPasswordChangeClaim_shouldReturnTrue_whenUserPasswordMustBeChanged() {
    testUser.setPasswordForChange(true);
    var token = jwtService.generateAccessToken(testUser);

    var result = jwtService.getPasswordChangeClaim(token);

    assertTrue(result);
  }

  @Test
  void getPasswordChangeClaim_shouldReturnFalse_whenUserPasswordDoesNotNeedToBeChanged() {
    testUser.setPasswordForChange(false);
    var token = jwtService.generateAccessToken(testUser);

    var result = jwtService.getPasswordChangeClaim(token);

    assertFalse(result);
  }

  @Test
  void getPasswordChangeClaim_shouldThrowException_whenTokenIsInvalid() {
    var invalidToken = "invalid.token.here";

    assertThrows(Exception.class, () -> jwtService.getPasswordChangeClaim(invalidToken));
  }

  private Claims parseToken(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
  }
}
