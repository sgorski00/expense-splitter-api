package pl.sgorski.expense_splitter.notification.dto;

import java.util.Set;
import java.util.UUID;
import org.jspecify.annotations.Nullable;

public record NotificationCommand(
    UUID userId, @Nullable String email, String title, String content, Set<Channel> channels) {
  public enum Channel {
    EMAIL,
    WEBSOCKET
  }
}
