package pl.sgorski.expense_splitter.security.config;

import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.sgorski.expense_splitter.features.auth.two_fa.config.TwoFactorProperties;
import pl.sgorski.expense_splitter.security.jwt.JwtProperties;

@Configuration
@RequiredArgsConstructor
public class SecurityKeyConfig {

  private final JwtProperties jwtProperties;
  private final TwoFactorProperties twoFactorProperties;

  @Bean("jwtSecretKey")
  public SecretKey secretKey() {
    return Keys.hmacShaKeyFor(jwtProperties.secretKey().getBytes(StandardCharsets.UTF_8));
  }

  @Bean("twoFaEncryptionKey")
  public SecretKey twoFaKey() {
    var decoded = Base64.getDecoder().decode(twoFactorProperties.encryptionKey());

    if (decoded.length != 32) {
      throw new IllegalArgumentException("2FA key must be 32 bytes");
    }

    return new SecretKeySpec(decoded, "AES");
  }
}
