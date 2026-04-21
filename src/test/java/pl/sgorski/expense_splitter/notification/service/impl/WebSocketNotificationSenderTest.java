package pl.sgorski.expense_splitter.notification.service.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.notification.domain.Notification;
import pl.sgorski.expense_splitter.notification.domain.NotificationChannel;
import pl.sgorski.expense_splitter.notification.dto.ws.NotificationWsDto;
import pl.sgorski.expense_splitter.notification.infrastructure.websocket.WebSocketSender;
import pl.sgorski.expense_splitter.notification.mapper.NotificationMapper;

@ExtendWith(MockitoExtension.class)
public class WebSocketNotificationSenderTest {

  @Mock private WebSocketSender webSocketSender;
  private WebSocketNotificationSender webSocketNotificationSender;
  private Notification notification;
  private User user;

  @BeforeEach
  void setUp() {
    var notificationMapper = Mappers.getMapper(NotificationMapper.class);
    webSocketNotificationSender =
        new WebSocketNotificationSender(webSocketSender, notificationMapper);

    user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail("test@example.com");
    notification = new Notification();
    notification.setId(UUID.randomUUID());
    notification.setUser(user);
    notification.setTitle("Test Title");
    notification.setBody("Test Body");
  }

  @Test
  void supports_shouldReturnTrue_whenChannelIsWebSocket() {
    var result = webSocketNotificationSender.supports(NotificationChannel.WEBSOCKET);

    assertTrue(result);
  }

  @Test
  void supports_shouldReturnFalse_whenChannelIsNotWebSocket() {
    var result = webSocketNotificationSender.supports(NotificationChannel.EMAIL);

    assertFalse(result);
  }

  @Test
  void send_shouldSendWebSocketNotification_whenNotificationIsValid() {
    webSocketNotificationSender.send(notification);

    verify(webSocketSender, times(1)).send(eq(user.getId()), any(NotificationWsDto.class));
  }
}
