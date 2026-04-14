package pl.sgorski.expense_splitter.notification.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import pl.sgorski.expense_splitter.notification.domain.NotificationChannel;
import pl.sgorski.expense_splitter.validator.text.NullOrNotBlank;

public record NotificationCommand(
    @NotNull UUID userId,
    @Nullable @NullOrNotBlank String email,
    @NotBlank String title,
    @NotBlank String content,
    @NotNull Set<NotificationChannel> channels) {}
