package pl.sgorski.expense_splitter.security.jwt;

import static org.junit.jupiter.api.Assertions.*;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JwtProviderTest {

  private SecretKey secretKey;
  private JwtProvider jwtProvider;

  private final String subject = "user-123";
  private final Duration duration = Duration.ofHours(1);

  @BeforeEach
  void setUp() {
    var secretKeyString = "MyVeryLongSecretKeyThatIsAtLeast32BytesLongForHS256Algorithm";
    secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    jwtProvider = new JwtProvider(secretKey);
  }

  @Test
  void generate_shouldGenerateValidToken_withCorrectStructure() {
    Map<String, Object> claims = Map.of("email", "test@example.com");

    var token = jwtProvider.generate(subject, duration, claims);

    assertNotNull(token);
    assertFalse(token.isBlank());
    assertTrue(token.contains("."));
  }

  @Test
  void generate_shouldEncodeSubject_inGeneratedToken() {
    Map<String, Object> claims = Map.of("email", "test@example.com");

    var token = jwtProvider.generate(subject, duration, claims);
    var parsedClaims = parseToken(token);

    assertEquals(subject, parsedClaims.getSubject());
  }

  @Test
  void generate_shouldIncludeAllClaims_inGeneratedToken() {
    Map<String, Object> claims =
        Map.of(
            "email", "test@example.com",
            "role", "USER",
            "passwordForChange", false);

    var token = jwtProvider.generate(subject, duration, claims);
    var parsedClaims = parseToken(token);

    assertEquals("test@example.com", parsedClaims.get("email", String.class));
    assertEquals("USER", parsedClaims.get("role", String.class));
    assertEquals(false, parsedClaims.get("passwordForChange", Boolean.class));
  }

  @Test
  void parse_shouldReturnClaims_whenTokenIsValid() {
    Map<String, Object> claims = Map.of("email", "test@example.com");
    var token = jwtProvider.generate(subject, duration, claims);

    var parsedClaims = jwtProvider.parse(token);

    assertNotNull(parsedClaims);
    assertEquals(subject, parsedClaims.getSubject());
    assertEquals("test@example.com", parsedClaims.get("email", String.class));
  }

  @Test
  void parse_shouldThrowException_whenTokenIsInvalid() {
    var invalidToken = "invalid.token.here";

    assertThrows(Exception.class, () -> jwtProvider.parse(invalidToken));
  }

  @Test
  void parse_shouldThrowException_whenTokenIsTampered() {
    Map<String, Object> claims = Map.of("email", "test@example.com");
    var token = jwtProvider.generate(subject, duration, claims);
    var tamperedToken = token.substring(0, token.length() - 1) + "X";

    assertThrows(Exception.class, () -> jwtProvider.parse(tamperedToken));
  }

  @Test
  void isInvalid_shouldReturnFalse_whenTokenIsValid() {
    Map<String, Object> claims = Map.of("email", "test@example.com");
    var token = jwtProvider.generate(subject, duration, claims);

    var result = jwtProvider.isInvalid(token);

    assertFalse(result);
  }

  @Test
  void isInvalid_shouldReturnTrue_whenTokenIsExpired() {
    var expiredDuration = Duration.ofMillis(-1000);
    Map<String, Object> claims = Map.of("email", "test@example.com");
    var token = jwtProvider.generate(subject, expiredDuration, claims);

    var result = jwtProvider.isInvalid(token);

    assertTrue(result);
  }

  @Test
  void isInvalid_shouldReturnTrue_whenTokenIsInvalid() {
    var invalidToken = "invalid.token.here";

    var result = jwtProvider.isInvalid(invalidToken);

    assertTrue(result);
  }

  private Claims parseToken(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
  }
}
