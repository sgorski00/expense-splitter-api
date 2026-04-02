package pl.sgorski.expense_splitter.security.config;

import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.sgorski.expense_splitter.security.jwt.JwtProperties;

@Configuration
@RequiredArgsConstructor
public class SecurityKeyConfig {

  private final JwtProperties jwtProperties;

  @Bean("jwtSecretKey")
  public SecretKey secretKey() {
    return Keys.hmacShaKeyFor(jwtProperties.secretKey().getBytes(StandardCharsets.UTF_8));
  }
}
