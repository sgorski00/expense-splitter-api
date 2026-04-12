package pl.sgorski.expense_splitter.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;
import org.jspecify.annotations.Nullable;

public record NotificationCommand(
    @NotNull UUID userId,
    @Nullable String email,
    @NotBlank String title,
    @NotBlank String content,
    @NotNull Set<Channel> channels) {
  public enum Channel {
    EMAIL,
    WEBSOCKET
  }
}
