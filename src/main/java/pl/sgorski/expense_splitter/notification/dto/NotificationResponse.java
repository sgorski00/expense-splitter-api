package pl.sgorski.expense_splitter.notification.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
    UUID userId, String title, String body, Boolean isRead, Instant createdAt) {}
