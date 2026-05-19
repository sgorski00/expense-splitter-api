package pl.sgorski.expense_splitter.security.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "es.cors")
public record CorsProperties(
        List<String> allowedOrigins
) { }
