package pl.sgorski.expense_splitter.features.auth.two_fa.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "two-factor")
public record TwoFactorProperties(String encryptionKey) {}
