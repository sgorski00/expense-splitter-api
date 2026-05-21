package pl.sgorski.expense_splitter.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public final class JwtProvider {

  private final SecretKey secretKey;

  public JwtProvider(@Qualifier("jwtSecretKey") SecretKey secretKey) {
    this.secretKey = secretKey;
  }

  public String generate(String subject, Duration expiration, Map<String, Object> claims) {
    var now = Instant.now();
    return Jwts.builder()
        .subject(subject)
        .claims(claims)
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plus(expiration)))
        .signWith(secretKey)
        .compact();
  }

  public Claims parse(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
  }

  public boolean isInvalid(String token) {
    try {
      return parse(token).getExpiration().before(new Date());
    } catch (Exception e) {
      return true;
    }
  }
}
