package pl.sgorski.expense_splitter.features.auth.oauth2.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.google")
public record GoogleOAuth2Properties(String clientId) {}
