package pl.sgorski.expense_splitter.notification.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import pl.sgorski.expense_splitter.features.friendship.event.FriendshipCreateEvent;
import pl.sgorski.expense_splitter.features.user.service.UserService;
import pl.sgorski.expense_splitter.notification.dto.command.NotificationCommand;
import pl.sgorski.expense_splitter.notification.service.NotificationPreferenceService;
import pl.sgorski.expense_splitter.notification.service.NotificationService;

@Component
@RequiredArgsConstructor
@Slf4j
public class FriendshipNotificationListener {

  private final NotificationService notificationService;
  private final NotificationPreferenceService preferenceService;
  private final UserService userService;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(FriendshipCreateEvent event) {
    log.debug("Handling new friend request for friendship {}", event.friendshipId());
    try {
      var recipient = userService.getUser(event.recipientId());
      var channels = preferenceService.getNotificationChannelsForUser(recipient);
      var command =
          new NotificationCommand(
              recipient.getId(),
              recipient.getEmail(),
              "New Friend Request",
              "You have received a new friend request.",
              channels);
      var notification = notificationService.create(command);

      notificationService.send(notification, command.channels());
    } catch (Exception ex) {
      log.error("Failed to send friendship create notification", ex);
    }
  }
}
