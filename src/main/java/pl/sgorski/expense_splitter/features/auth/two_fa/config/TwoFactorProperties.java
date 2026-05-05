package pl.sgorski.expense_splitter.features.auth.two_fa.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "two-factor")
public record TwoFactorProperties(
    @NotBlank @Pattern(regexp = "^[A-Za-z0-9+/=]{44}$") String encryptionKey) {}
