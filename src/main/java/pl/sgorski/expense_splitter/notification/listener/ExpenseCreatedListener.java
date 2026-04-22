package pl.sgorski.expense_splitter.notification.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import pl.sgorski.expense_splitter.features.expense.event.ExpenseCreatedEvent;
import pl.sgorski.expense_splitter.features.user.service.UserService;
import pl.sgorski.expense_splitter.notification.dto.command.NotificationCommand;
import pl.sgorski.expense_splitter.notification.service.NotificationPreferenceService;
import pl.sgorski.expense_splitter.notification.service.NotificationService;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExpenseCreatedListener {

  private final NotificationService notificationService;
  private final NotificationPreferenceService preferenceService;
  private final UserService userService;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(ExpenseCreatedEvent event) {
    log.debug("Handling new expense created event");
    try {
      var author = userService.getUser(event.authorId());
      var participants = userService.getUsers(event.participantsId());
      participants.forEach(
          participant -> {
            try {
              var channels = preferenceService.getNotificationChannelsForUser(participant);
              var command =
                  new NotificationCommand(
                      participant.getId(),
                      participant.getEmail(),
                      "New Expense Created",
                      String.format(
                          "%s has created a new expense that you are a part of.%n%nThe total amount to pay is %s divided for %d participant",
                          author.getDisplayName(), event.amount(), participants.size()));
              var notification = notificationService.create(command);

              notificationService.send(notification, channels);
            } catch (Exception ex) {
              log.error(
                  "Failed to send expense created notification to user with id: {}",
                  participant.getId(),
                  ex);
            }
          });
    } catch (Exception ex) {
      log.error("Failed to send expense created notification", ex);
    }
  }
}
