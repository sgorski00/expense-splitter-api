package pl.sgorski.expense_splitter.notification.listener;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.expense_splitter.features.friendship.event.FriendshipCreateEvent;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.service.UserService;
import pl.sgorski.expense_splitter.notification.domain.Notification;
import pl.sgorski.expense_splitter.notification.domain.NotificationChannel;
import pl.sgorski.expense_splitter.notification.dto.command.NotificationCommand;
import pl.sgorski.expense_splitter.notification.service.NotificationPreferenceService;
import pl.sgorski.expense_splitter.notification.service.NotificationService;

@ExtendWith(MockitoExtension.class)
public class FriendshipNotificationListenerTest {

  @Mock private NotificationService notificationService;
  @Mock private NotificationPreferenceService preferenceService;
  @Mock private UserService userService;
  @InjectMocks private FriendshipNotificationListener listener;
  private User recipient;
  private User requester;
  private Notification notification;

  @BeforeEach
  void setUp() {
    recipient = new User();
    recipient.setId(UUID.randomUUID());
    recipient.setEmail("recipient@example.com");
    requester = new User();
    requester.setId(UUID.randomUUID());
    recipient.setEmail("requester@example.com");
    notification = new Notification();
    notification.setId(UUID.randomUUID());
    notification.setUser(recipient);
    notification.setTitle("New Friend Request");
  }

  @Test
  void handle_shouldCreateAndSendNotification_whenEventIsValid() {
    var friendshipId = UUID.randomUUID();
    var event = new FriendshipCreateEvent(friendshipId, requester.getId(), recipient.getId());
    var channels = new HashSet<>(List.of(NotificationChannel.EMAIL));

    when(userService.getUser(recipient.getId())).thenReturn(recipient);
    when(userService.getUser(requester.getId())).thenReturn(requester);
    when(preferenceService.getNotificationChannelsForUser(recipient)).thenReturn(channels);
    when(notificationService.create(any(NotificationCommand.class))).thenReturn(notification);

    listener.handle(event);

    verify(userService, times(1)).getUser(recipient.getId());
    verify(preferenceService, times(1)).getNotificationChannelsForUser(recipient);
    verify(notificationService, times(1)).create(any(NotificationCommand.class));
    verify(notificationService, times(1)).send(notification, channels);
  }

  @Test
  void handle_shouldHandleException_whenErrorOccursDuringNotificationCreation() {
    var friendshipId = UUID.randomUUID();
    var event = new FriendshipCreateEvent(friendshipId, requester.getId(), recipient.getId());
    when(userService.getUser(requester.getId())).thenReturn(requester);

    when(userService.getUser(recipient.getId())).thenThrow(new RuntimeException("User not found"));

    listener.handle(event);

    verify(userService, times(1)).getUser(recipient.getId());
    verify(notificationService, never()).create(any());
  }

  @Test
  void handle_shouldHandleException_whenErrorOccursDuringSending() {
    var friendshipId = UUID.randomUUID();
    var event = new FriendshipCreateEvent(friendshipId, requester.getId(), recipient.getId());
    var channels = new HashSet<NotificationChannel>();

    when(userService.getUser(requester.getId())).thenReturn(requester);
    when(userService.getUser(recipient.getId())).thenReturn(recipient);
    when(preferenceService.getNotificationChannelsForUser(recipient)).thenReturn(channels);
    when(notificationService.create(any(NotificationCommand.class))).thenReturn(notification);
    doThrow(new RuntimeException("Send failed"))
        .when(notificationService)
        .send(notification, channels);

    listener.handle(event);

    verify(notificationService, times(1)).send(notification, channels);
  }
}
