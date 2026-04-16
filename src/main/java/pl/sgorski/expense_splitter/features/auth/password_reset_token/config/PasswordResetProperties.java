package pl.sgorski.expense_splitter.features.auth.password_reset_token.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "password-reset")
public record PasswordResetProperties(Long tokenExpirationTimeInMs, String frontendUrl) {}
