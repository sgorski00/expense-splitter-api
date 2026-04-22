package pl.sgorski.expense_splitter.notification.listener;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.expense_splitter.features.expense.event.ExpenseCreatedEvent;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.service.UserService;
import pl.sgorski.expense_splitter.notification.domain.Notification;
import pl.sgorski.expense_splitter.notification.domain.NotificationChannel;
import pl.sgorski.expense_splitter.notification.dto.command.NotificationCommand;
import pl.sgorski.expense_splitter.notification.service.NotificationPreferenceService;
import pl.sgorski.expense_splitter.notification.service.NotificationService;

@ExtendWith(MockitoExtension.class)
public class ExpenseCreatedListenerTest {

  @Mock private NotificationService notificationService;
  @Mock private NotificationPreferenceService preferenceService;
  @Mock private UserService userService;
  @InjectMocks private ExpenseCreatedListener listener;

  private User author;
  private User participant1;
  private User participant2;
  private Notification notification;

  @BeforeEach
  void setUp() {
    author = new User();
    author.setId(UUID.randomUUID());
    author.setEmail("author@example.com");
    author.setFirstName("John");
    author.setLastName("Doe");

    participant1 = new User();
    participant1.setId(UUID.randomUUID());
    participant1.setEmail("participant1@example.com");

    participant2 = new User();
    participant2.setId(UUID.randomUUID());
    participant2.setEmail("participant2@example.com");

    notification = new Notification();
    notification.setId(UUID.randomUUID());
    notification.setUser(participant1);
    notification.setTitle("New Expense Created");
  }

  @Test
  void handle_shouldCreateAndSendNotification_whenEventIsValidWithSingleParticipant() {
    var event =
        new ExpenseCreatedEvent(
            author.getId(),
            Set.of(participant1.getId()),
            "Dinner",
            "Restaurant bill",
            new BigDecimal("100.00"),
            Instant.now());
    var channels = new HashSet<>(List.of(NotificationChannel.EMAIL));

    when(userService.getUser(author.getId())).thenReturn(author);
    when(userService.getUsers(Set.of(participant1.getId()))).thenReturn(List.of(participant1));
    when(preferenceService.getNotificationChannelsForUser(participant1)).thenReturn(channels);
    when(notificationService.create(any(NotificationCommand.class))).thenReturn(notification);

    listener.handle(event);

    verify(userService, times(1)).getUser(author.getId());
    verify(userService, times(1)).getUsers(Set.of(participant1.getId()));
    verify(preferenceService, times(1)).getNotificationChannelsForUser(participant1);
    verify(notificationService, times(1)).create(any(NotificationCommand.class));
    verify(notificationService, times(1)).send(notification, channels);
  }

  @Test
  void handle_shouldCreateAndSendMultipleNotifications_whenEventHasMultipleParticipants() {
    var event =
        new ExpenseCreatedEvent(
            author.getId(),
            Set.of(participant1.getId(), participant2.getId()),
            "Trip",
            "Gas and hotel",
            new BigDecimal("500.00"),
            Instant.now());
    var channels = new HashSet<>(List.of(NotificationChannel.EMAIL, NotificationChannel.WEBSOCKET));

    when(userService.getUser(author.getId())).thenReturn(author);
    when(userService.getUsers(Set.of(participant1.getId(), participant2.getId())))
        .thenReturn(List.of(participant1, participant2));
    when(preferenceService.getNotificationChannelsForUser(any(User.class))).thenReturn(channels);
    when(notificationService.create(any(NotificationCommand.class))).thenReturn(notification);

    listener.handle(event);

    verify(userService, times(1)).getUser(author.getId());
    verify(userService, times(1)).getUsers(Set.of(participant1.getId(), participant2.getId()));
    verify(preferenceService, times(2)).getNotificationChannelsForUser(any(User.class));
    verify(notificationService, times(2)).create(any(NotificationCommand.class));
    verify(notificationService, times(2)).send(eq(notification), eq(channels));
  }

  @Test
  void handle_shouldHandleException_whenAuthorNotFound() {
    var event =
        new ExpenseCreatedEvent(
            author.getId(),
            Set.of(participant1.getId()),
            "Test Expense",
            null,
            new BigDecimal("100.00"),
            Instant.now());

    when(userService.getUser(author.getId())).thenThrow(new RuntimeException("Author not found"));

    listener.handle(event);

    verify(userService, times(1)).getUser(author.getId());
    verify(userService, never()).getUsers(any());
    verify(notificationService, never()).create(any());
    verify(notificationService, never()).send(any(), any());
  }

  @Test
  void handle_shouldHandleException_whenParticipantsNotFound() {
    var event =
        new ExpenseCreatedEvent(
            author.getId(),
            Set.of(participant1.getId()),
            "Test Expense",
            null,
            new BigDecimal("100.00"),
            Instant.now());

    when(userService.getUser(author.getId())).thenReturn(author);
    when(userService.getUsers(Set.of(participant1.getId()))).thenReturn(List.of());

    listener.handle(event);

    verify(userService, times(1)).getUser(author.getId());
    verify(userService, times(1)).getUsers(Set.of(participant1.getId()));
    verify(notificationService, never()).create(any());
    verify(notificationService, never()).send(any(), any());
  }

  @Test
  void handle_shouldHandleException_whenNotificationCreationFails() {
    var event =
        new ExpenseCreatedEvent(
            author.getId(),
            Set.of(participant1.getId()),
            "Test Expense",
            null,
            new BigDecimal("100.00"),
            Instant.now());
    var channels = new HashSet<>(List.of(NotificationChannel.EMAIL));

    when(userService.getUser(author.getId())).thenReturn(author);
    when(userService.getUsers(Set.of(participant1.getId()))).thenReturn(List.of(participant1));
    when(preferenceService.getNotificationChannelsForUser(participant1)).thenReturn(channels);
    when(notificationService.create(any(NotificationCommand.class)))
        .thenThrow(new RuntimeException("Notification creation failed"));

    listener.handle(event);

    verify(notificationService, times(1)).create(any(NotificationCommand.class));
    verify(notificationService, never()).send(any(), any());
  }

  @Test
  void handle_shouldHandleException_whenErrorOccursDuringSending() {
    var event =
        new ExpenseCreatedEvent(
            author.getId(),
            Set.of(participant1.getId()),
            "Test Expense",
            null,
            new BigDecimal("100.00"),
            Instant.now());
    var channels = new HashSet<>(List.of(NotificationChannel.EMAIL));

    when(userService.getUser(author.getId())).thenReturn(author);
    when(userService.getUsers(Set.of(participant1.getId()))).thenReturn(List.of(participant1));
    when(preferenceService.getNotificationChannelsForUser(participant1)).thenReturn(channels);
    when(notificationService.create(any(NotificationCommand.class))).thenReturn(notification);
    doThrow(new RuntimeException("Send failed")).when(notificationService).send(any(), any());

    listener.handle(event);

    verify(notificationService, times(1)).create(any(NotificationCommand.class));
    verify(notificationService, times(1)).send(notification, channels);
  }
}
