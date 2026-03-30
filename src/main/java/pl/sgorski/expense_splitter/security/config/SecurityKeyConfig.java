package pl.sgorski.expense_splitter.security.config;

import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.sgorski.expense_splitter.security.jwt.JwtProperties;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
@RequiredArgsConstructor
public class SecurityKeyConfig {

    private final JwtProperties jwtProperties;

    @Bean("jwtSecretKey")
    public SecretKey secretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.secretKey().getBytes(StandardCharsets.UTF_8));
    }
}
