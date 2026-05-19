package pl.sgorski.expense_splitter.notification.infrastructure.websocket.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import pl.sgorski.expense_splitter.notification.dto.ws.NotificationWsDto;
import pl.sgorski.expense_splitter.notification.infrastructure.websocket.WebSocketSender;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "es.websocket.enabled", havingValue = "true")
public class WebSocketSenderImpl implements WebSocketSender {

  private final SimpMessagingTemplate template;

  @Override
  public void send(UUID userId, NotificationWsDto message) {
    template.convertAndSendToUser(userId.toString(), "/notifications", message);
  }
}
