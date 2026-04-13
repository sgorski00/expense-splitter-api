package pl.sgorski.expense_splitter.notification.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.notification.domain.Notification;
import pl.sgorski.expense_splitter.notification.dto.NotificationChannel;
import pl.sgorski.expense_splitter.notification.infrastructure.email.EmailSender;

@ExtendWith(MockitoExtension.class)
public class EmailNotificationSenderTest {

  @Mock private EmailSender emailSender;
  @InjectMocks private EmailNotificationSender emailNotificationSender;
  private Notification notification;
  private User user;

  @BeforeEach
  void setUp() {
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
  void supports_shouldReturnTrue_whenChannelIsEmail() {
    var result = emailNotificationSender.supports(NotificationChannel.EMAIL);

    assertTrue(result);
  }

  @Test
  void supports_shouldReturnFalse_whenChannelIsNotEmail() {
    var result = emailNotificationSender.supports(NotificationChannel.WEBSOCKET);

    assertFalse(result);
  }

  @Test
  void send_shouldSendEmailNotification_whenNotificationIsValid() {
    emailNotificationSender.send(notification);

    verify(emailSender, times(1))
        .send(eq(user.getEmail()), eq(notification.getTitle()), eq(notification.getBody()));
  }
}
