package pl.sgorski.expense_splitter.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "Notification Response", description = "Notification details returned by API.")
public record NotificationResponse(
    @Schema(
            description = "User unique identifier who received the notification.",
            example = "123e4567-e89b-12d3-a456-426614174000")
        UUID userId,
    @Schema(description = "Notification title.", example = "Expense Settlement") String title,
    @Schema(
            description = "Notification content body.",
            example = "John Doe paid $50.00 for shared expense.")
        String body,
    @Schema(description = "Whether the notification has been read.", example = "false")
        Boolean isRead,
    @Schema(description = "Notification creation timestamp.", example = "2026-04-13T14:30:00Z")
        Instant createdAt) {}
