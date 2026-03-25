package pl.sgorski.expense_splitter.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.features.user.domain.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

@Service
public final class JwtService {

    private static final String PASSWORD_CHANGE_REQUIRED_CLAIM = "passwordForChange";
    private static final String EMAIL_CLAIM = "email";
    private static final String ROLE_CLAIM = "roles";

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(this.jwtProperties.secretKey().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user) {
        var now = Instant.now();
        var expirationTime = now.plusMillis(jwtProperties.expirationTimeInMs());
        var authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return Jwts
                .builder()
                .subject(String.valueOf(user.getId()))
                .claim(EMAIL_CLAIM, user.getEmail())
                .claim(ROLE_CLAIM, authorities)
                .claim(PASSWORD_CHANGE_REQUIRED_CLAIM, user.isPasswordForChange())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expirationTime))
                .signWith(this.secretKey)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            var claims = getClaimsFromToken(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        return Objects.requireNonNull(getClaimsFromToken(token)
                .get(EMAIL_CLAIM, String.class));
    }

    public Boolean getPasswordChangeClaim(String token) {
        return Objects.requireNonNull(getClaimsFromToken(token)
                .get(PASSWORD_CHANGE_REQUIRED_CLAIM, Boolean.class));
    }

    public long getExpirationSecond() {
        return jwtProperties.expirationTimeInMs() / 1000;
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(this.secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
