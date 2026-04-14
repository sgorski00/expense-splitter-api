package pl.sgorski.expense_splitter.notification.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.expense_splitter.exceptions.notification.NotificationNotFoundException;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.service.UserService;
import pl.sgorski.expense_splitter.notification.domain.Notification;
import pl.sgorski.expense_splitter.notification.domain.NotificationChannel;
import pl.sgorski.expense_splitter.notification.dto.command.NotificationCommand;
import pl.sgorski.expense_splitter.notification.repository.NotificationRepository;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

  @Mock private NotificationRepository repository;
  @Mock private UserService userService;
  @InjectMocks private NotificationService notificationService;
  private User user;
  private Notification notification;
  private UUID userId;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    user = new User();
    user.setId(userId);
    user.setEmail("test@example.com");
    notification = new Notification();
    notification.setId(UUID.randomUUID());
    notification.setUser(user);
    notification.setTitle("Test Title");
    notification.setBody("Test Body");
    notification.setIsRead(false);
  }

  @Test
  void create_shouldCreateNotification_whenCommandIsValid() {
    var command =
        new NotificationCommand(userId, user.getEmail(), "Title", "Content", new HashSet<>());
    when(userService.getUser(userId)).thenReturn(user);
    when(repository.save(any(Notification.class))).thenReturn(notification);

    var result = notificationService.create(command);

    assertNotNull(result);
    assertEquals(notification, result);
    verify(userService, times(1)).getUser(userId);
    verify(repository, times(1)).save(any(Notification.class));
  }

  @Test
  void markAsRead_shouldMarkNotificationAsRead_whenNotificationExists() {
    when(repository.findById(notification.getId())).thenReturn(Optional.of(notification));
    when(repository.save(any(Notification.class))).thenReturn(notification);

    var result = notificationService.markAsRead(notification.getId(), user);

    assertTrue(result.getIsRead());
    verify(repository, times(1)).findById(notification.getId());
    verify(repository, times(1)).save(notification);
  }

  @Test
  void markAsRead_shouldThrowNotificationNotFoundException_whenNotificationNotFound() {
    when(repository.findById(notification.getId())).thenReturn(Optional.empty());

    assertThrows(
        NotificationNotFoundException.class,
        () -> notificationService.markAsRead(notification.getId(), user));
    verify(repository, times(1)).findById(notification.getId());
    verify(repository, never()).save(any());
  }

  @Test
  void markAsRead_shouldThrowNotificationNotFoundException_whenUserIsNotOwner() {
    var otherUser = new User();
    otherUser.setId(UUID.randomUUID());
    when(repository.findById(notification.getId())).thenReturn(Optional.of(notification));

    assertThrows(
        NotificationNotFoundException.class,
        () -> notificationService.markAsRead(notification.getId(), otherUser));
    verify(repository, never()).save(any());
  }

  @Test
  void send_shouldCallSender_whenChannelsContainEmail() {
    var channel = NotificationChannel.EMAIL;
    var channels = new HashSet<NotificationChannel>();
    channels.add(channel);
    var mockSender = mock(NotificationSender.class);
    when(mockSender.supports(channel)).thenReturn(true);
    var senderList = List.of(mockSender);
    notificationService = new NotificationService(senderList, repository, userService);

    notificationService.send(notification, channels);

    verify(mockSender, times(1)).send(notification);
  }
}
