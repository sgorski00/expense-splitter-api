package pl.sgorski.expense_splitter.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.mail")
public record MailProperties(String username, String defaultEncoding) {}
