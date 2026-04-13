package pl.sgorski.expense_splitter.notification.listener;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
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
import pl.sgorski.expense_splitter.notification.dto.NotificationChannel;
import pl.sgorski.expense_splitter.notification.dto.NotificationCommand;
import pl.sgorski.expense_splitter.notification.service.NotificationService;

@ExtendWith(MockitoExtension.class)
public class FriendshipNotificationListenerTest {

  @Mock private NotificationService notificationService;
  @Mock private UserService userService;
  @InjectMocks private FriendshipNotificationListener listener;
  private User recipient;
  private Notification notification;

  @BeforeEach
  void setUp() {
    recipient = new User();
    recipient.setId(UUID.randomUUID());
    recipient.setEmail("recipient@example.com");
    notification = new Notification();
    notification.setId(UUID.randomUUID());
    notification.setUser(recipient);
    notification.setTitle("New Friend Request");
  }

  @Test
  void handle_shouldCreateAndSendNotification_whenEventIsValid() {
    var senderId = UUID.randomUUID();
    var friendshipId = UUID.randomUUID();
    var event = new FriendshipCreateEvent(friendshipId, senderId, recipient.getId());
    var command =
        new NotificationCommand(
            recipient.getId(),
            recipient.getEmail(),
            "New Friend Request",
            "You have received a new friend request.",
            new HashSet<>(java.util.List.of(NotificationChannel.EMAIL)));

    when(userService.getUser(recipient.getId())).thenReturn(recipient);
    when(notificationService.getNewFriendRequestCommand(recipient)).thenReturn(command);
    when(notificationService.create(command)).thenReturn(notification);

    listener.handle(event);

    verify(userService, times(1)).getUser(recipient.getId());
    verify(notificationService, times(1)).getNewFriendRequestCommand(recipient);
    verify(notificationService, times(1)).create(command);
    verify(notificationService, times(1)).send(notification, command.channels());
  }

  @Test
  void handle_shouldHandleException_whenErrorOccursDuringNotificationCreation() {
    var senderId = UUID.randomUUID();
    var friendshipId = UUID.randomUUID();
    var event = new FriendshipCreateEvent(friendshipId, senderId, recipient.getId());

    when(userService.getUser(recipient.getId())).thenThrow(new RuntimeException("User not found"));

    listener.handle(event);

    verify(userService, times(1)).getUser(recipient.getId());
    verify(notificationService, never()).create(any());
  }

  @Test
  void handle_shouldHandleException_whenErrorOccursDuringSending() {
    var senderId = UUID.randomUUID();
    var friendshipId = UUID.randomUUID();
    var event = new FriendshipCreateEvent(friendshipId, senderId, recipient.getId());
    var command =
        new NotificationCommand(
            recipient.getId(),
            recipient.getEmail(),
            "New Friend Request",
            "You have received a new friend request.",
            new HashSet<>());

    when(userService.getUser(recipient.getId())).thenReturn(recipient);
    when(notificationService.getNewFriendRequestCommand(recipient)).thenReturn(command);
    when(notificationService.create(command)).thenReturn(notification);
    doThrow(new RuntimeException("Send failed"))
        .when(notificationService)
        .send(notification, command.channels());

    listener.handle(event);

    verify(notificationService, times(1)).send(notification, command.channels());
  }
}
