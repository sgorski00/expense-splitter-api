package pl.sgorski.expense_splitter.features.auth.two_fa.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

@Profile("test")
@ConfigurationProperties(prefix = "app.test.totp")
public record TestTotpProperties(int code, String secret) {}
