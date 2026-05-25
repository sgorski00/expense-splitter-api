package pl.sgorski.expense_splitter.security.oauth2;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.security.jwt.JwtProvider;

@Service
@RequiredArgsConstructor
public final class OAuth2ContextService {

  private static final Duration OAUTH2_CONTEXT_EXPIRATION = Duration.ofMinutes(5);

  private final JwtProvider jwtProvider;

  public String generate(UUID userId, OAuth2Mode mode) {
    return jwtProvider.generate(
        String.valueOf(userId), OAUTH2_CONTEXT_EXPIRATION, Map.of("mode", mode));
  }

  public OAuth2ContextPayload parse(String token) {
    var claims = jwtProvider.parse(token);
    var userId = UUID.fromString(claims.getSubject());
    var mode = OAuth2Mode.fromString(claims.get("mode", String.class));
    return new OAuth2ContextPayload(userId, mode);
  }
}
