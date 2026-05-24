package pl.sgorski.expense_splitter.security.oauth2;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "es.oauth2")
public record OAuth2Properties(String frontendRedirectUrl) {}
