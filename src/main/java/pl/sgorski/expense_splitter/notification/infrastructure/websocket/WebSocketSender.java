package pl.sgorski.expense_splitter.notification.infrastructure.websocket;

import pl.sgorski.expense_splitter.notification.dto.ws.NotificationWsDto;

import java.util.UUID;

public interface WebSocketSender {

  void send(UUID userId, NotificationWsDto message);
}
