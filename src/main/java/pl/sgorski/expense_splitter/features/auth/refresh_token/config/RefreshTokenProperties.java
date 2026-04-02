package pl.sgorski.expense_splitter.features.auth.refresh_token.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "refresh-token")
public record RefreshTokenProperties(Long expirationTimeInMs) {}
