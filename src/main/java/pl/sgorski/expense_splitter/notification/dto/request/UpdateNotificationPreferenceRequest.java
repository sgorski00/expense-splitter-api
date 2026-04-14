package pl.sgorski.expense_splitter.notification.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(
    name = "Update Notification Preference Request",
    description =
        "Payload used to update notification channel preferences. All fields are optional. If a field is not provided, the corresponding preference will remain unchanged.")
public record UpdateNotificationPreferenceRequest(
    @Schema(description = "Enable or disable email notifications.", example = "true")
        @Nullable Boolean emailNotificationsEnabled,
    @Schema(description = "Enable or disable websocket notifications.", example = "true")
        @Nullable Boolean websocketNotificationsEnabled) {}
