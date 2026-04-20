package pl.sgorski.expense_splitter.notification.infrastructure.websocket;

import java.util.UUID;

public interface WebSocketSender {

  void send(UUID userId, String message);
}
