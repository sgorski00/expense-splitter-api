package pl.sgorski.expense_splitter.notification.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(
    name = "Notification Preference Response",
    description = "Current notification channel preferences for a user.")
public record NotificationPreferenceResponse(
    @Schema(
            description = "User unique identifier.",
            example = "123e4567-e89b-12d3-a456-426614174000")
        UUID userId,
    @Schema(description = "Email notifications enabled status.", example = "true")
        Boolean emailNotificationsEnabled,
    @Schema(description = "Websocket notifications enabled status.", example = "true")
        Boolean websocketNotificationsEnabled,
    @Schema(description = "Preference creation timestamp.", example = "2026-04-13T14:30:00Z")
        Instant createdAt,
    @Schema(description = "Preference last update timestamp.", example = "2026-04-13T15:45:00Z")
        Instant updatedAt) {}
