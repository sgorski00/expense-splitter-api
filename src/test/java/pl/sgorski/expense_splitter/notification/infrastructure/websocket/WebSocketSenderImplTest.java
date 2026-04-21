package pl.sgorski.expense_splitter.notification.infrastructure.websocket;

import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import pl.sgorski.expense_splitter.notification.dto.ws.NotificationWsDto;
import pl.sgorski.expense_splitter.notification.infrastructure.websocket.impl.WebSocketSenderImpl;

@ExtendWith(MockitoExtension.class)
public class WebSocketSenderImplTest {

  @Mock private SimpMessagingTemplate simpMessagingTemplate;
  @InjectMocks private WebSocketSenderImpl webSocketSender;

  @Test
  void send_shouldSendEmail_whenPropertiesAreValid() {
    var userId = UUID.randomUUID();
    var notificationWsDto =
        new NotificationWsDto(UUID.randomUUID(), "Test Title", "Test Body", false, Instant.now());

    webSocketSender.send(userId, notificationWsDto);

    verify(simpMessagingTemplate, times(1))
        .convertAndSendToUser(
            eq(userId.toString()), eq("/queue/notifications"), eq(notificationWsDto));
  }
}
