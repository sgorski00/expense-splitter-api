package pl.sgorski.expense_splitter.notification.dto.command;

import org.jspecify.annotations.Nullable;

public record UpdateNotificationRequestCommand(
    @Nullable Boolean emailNotificationsEnabled, @Nullable Boolean websocketNotificationsEnabled) {}
