package pl.sgorski.expense_splitter.notification.infrastructure.websocket;

import java.util.UUID;
import pl.sgorski.expense_splitter.notification.dto.ws.NotificationWsDto;

public interface WebSocketSender {

  void send(UUID userId, NotificationWsDto message);
}
