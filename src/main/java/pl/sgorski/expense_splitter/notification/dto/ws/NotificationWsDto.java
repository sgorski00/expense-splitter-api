package pl.sgorski.expense_splitter.notification.dto.ws;

import java.time.Instant;
import java.util.UUID;

public record NotificationWsDto(
        UUID id,
        String title,
        String body,
        Boolean isRead,
        Instant createdAt
) {
}
