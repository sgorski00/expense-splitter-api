package pl.sgorski.expense_splitter.notification.listener;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pl.sgorski.expense_splitter.exceptions.user.UserNotFoundException;
import pl.sgorski.expense_splitter.features.auth.event.FailedLoginAttemptEvent;
import pl.sgorski.expense_splitter.features.user.service.UserService;
import pl.sgorski.expense_splitter.notification.domain.NotificationChannel;
import pl.sgorski.expense_splitter.notification.dto.command.NotificationCommand;
import pl.sgorski.expense_splitter.notification.service.NotificationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class FailedLoginAttemptListener {

  private final UserService userService;
  private final NotificationService notificationService;

  @Async
  @EventListener
  public void handle(FailedLoginAttemptEvent event) {
    try {
      var user = userService.getUser(event.email());
      var channels = Set.of(NotificationChannel.EMAIL);
      var command =
          new NotificationCommand(
              user.getId(),
              user.getEmail(),
              "Failed Login Attempt",
              String.format(
                  "There was a failed login attempt on your account at %s.%n%nIf this wasn't you, please change your password as soon as possible.",
                  event.attemptedAt()));
      var notification = notificationService.create(command);
      notificationService.send(notification, channels);
    } catch (UserNotFoundException e) {
      log.warn("Failed login attempt for non-existent user with email: {}", event.email());
    } catch (Exception e) {
      log.error("Failed to handle failed login attempt event", e);
    }
  }
}
