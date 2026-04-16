package pl.sgorski.expense_splitter.notification.listener;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.expense_splitter.features.auth.password_reset_token.config.PasswordResetProperties;
import pl.sgorski.expense_splitter.features.auth.password_reset_token.event.PasswordResetRequestEvent;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.service.UserService;
import pl.sgorski.expense_splitter.notification.domain.Notification;
import pl.sgorski.expense_splitter.notification.domain.NotificationChannel;
import pl.sgorski.expense_splitter.notification.dto.command.NotificationCommand;
import pl.sgorski.expense_splitter.notification.service.NotificationService;

@ExtendWith(MockitoExtension.class)
public class PasswordResetRequestListenerTest {

  @Mock private PasswordResetProperties passwordResetProperties;
  @Mock private NotificationService notificationService;
  @Mock private UserService userService;
  @InjectMocks private PasswordResetRequestListener listener;

  private User user;
  private Notification notification;
  private PasswordResetRequestEvent event;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail("user@example.com");

    notification = new Notification();
    notification.setId(UUID.randomUUID());
    notification.setUser(user);
    notification.setTitle("Password reset request");

    var tokenUUID = UUID.randomUUID();
    event = new PasswordResetRequestEvent(tokenUUID, Instant.now(), user.getId());
  }

  @Test
  void handle_shouldCreateAndSendNotification_whenEventIsValid() {
    when(userService.getUser(user.getId())).thenReturn(user);
    when(passwordResetProperties.frontendUrl()).thenReturn("http://localhost:3000/password-reset");
    when(notificationService.create(any(NotificationCommand.class))).thenReturn(notification);

    listener.handle(event);

    verify(userService, times(1)).getUser(user.getId());
    verify(passwordResetProperties, times(1)).frontendUrl();
    verify(notificationService, times(1)).create(any(NotificationCommand.class));
    verify(notificationService, times(1))
        .send(eq(notification), argThat(channels -> channels.contains(NotificationChannel.EMAIL)));
  }

  @Test
  void handle_shouldHandleException_whenUserNotFound() {
    when(userService.getUser(user.getId())).thenThrow(new RuntimeException("User not found"));

    listener.handle(event);

    verify(userService, times(1)).getUser(user.getId());
    verify(notificationService, never()).create(any());
    verify(notificationService, never()).send(any(), any());
  }

  @Test
  void handle_shouldHandleException_whenErrorOccursDuringSending() {
    when(userService.getUser(user.getId())).thenReturn(user);
    when(passwordResetProperties.frontendUrl()).thenReturn("http://localhost:3000/password-reset");
    when(notificationService.create(any(NotificationCommand.class))).thenReturn(notification);
    doThrow(new RuntimeException("Send failed"))
        .when(notificationService)
        .send(any(Notification.class), anySet());

    listener.handle(event);

    verify(userService, times(1)).getUser(user.getId());
    verify(notificationService, times(1)).create(any(NotificationCommand.class));
    verify(notificationService, times(1)).send(any(Notification.class), anySet());
  }
}
