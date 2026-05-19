package pl.sgorski.expense_splitter.security.config.properties;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "es.cors")
public record CorsProperties(List<String> allowedOrigins) {}
