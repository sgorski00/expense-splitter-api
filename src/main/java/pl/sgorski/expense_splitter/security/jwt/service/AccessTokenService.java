package pl.sgorski.expense_splitter.security.jwt.service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.security.jwt.config.JwtProperties;
import pl.sgorski.expense_splitter.security.jwt.payload.AccessTokenPayload;

@Service
@RequiredArgsConstructor
public final class AccessTokenService {

  private static final String PASSWORD_CHANGE_REQUIRED_CLAIM = "passwordForChange";
  private static final String TWO_FACTOR_REQUIRED_CLAIM = "twoFactorRequired";
  private static final String EMAIL_CLAIM = "email";
  private static final String ROLE_CLAIM = "roles";

  private final JwtProvider jwtProvider;
  private final JwtProperties jwtProperties;

  public String generate(User user, boolean twoFactorPending) {
    var expiration = Duration.of(jwtProperties.expirationTimeInMs(), ChronoUnit.MILLIS);
    var authorities = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
    return jwtProvider.generate(
        String.valueOf(user.getId()),
        expiration,
        Map.of(
            EMAIL_CLAIM,
            user.getEmail(),
            ROLE_CLAIM,
            authorities,
            PASSWORD_CHANGE_REQUIRED_CLAIM,
            user.isPasswordForChange(),
            TWO_FACTOR_REQUIRED_CLAIM,
            twoFactorPending));
  }

  public AccessTokenPayload parse(String token) {
    var claims = jwtProvider.parse(token);
    return new AccessTokenPayload(
        UUID.fromString(claims.getSubject()),
        Objects.requireNonNull(claims.get(EMAIL_CLAIM, String.class)),
        Objects.requireNonNull(claims.get(PASSWORD_CHANGE_REQUIRED_CLAIM, Boolean.class)),
        Objects.requireNonNull(claims.get(TWO_FACTOR_REQUIRED_CLAIM, Boolean.class)));
  }
}
