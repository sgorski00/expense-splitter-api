package pl.sgorski.expense_splitter.notification.listener;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.util.UriComponentsBuilder;
import pl.sgorski.expense_splitter.features.auth.password_reset_token.config.PasswordResetProperties;
import pl.sgorski.expense_splitter.features.auth.password_reset_token.event.PasswordResetRequestEvent;
import pl.sgorski.expense_splitter.features.user.service.UserService;
import pl.sgorski.expense_splitter.notification.domain.NotificationChannel;
import pl.sgorski.expense_splitter.notification.dto.command.NotificationCommand;
import pl.sgorski.expense_splitter.notification.service.NotificationService;

@Component
@RequiredArgsConstructor
@Slf4j
public class PasswordResetRequestListener {

  private final PasswordResetProperties passwordResetProperties;
  private final NotificationService notificationService;
  private final UserService userService;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(PasswordResetRequestEvent event) {
    log.debug("Handling new password reset request for user: {}", event.userId());
    try {
      var recipient = userService.getUser(event.userId());
      var redirectUrl = getFrontendPasswordResetUrl(event);
      var channels = Set.of(NotificationChannel.EMAIL);
      var command =
          new NotificationCommand(
              recipient.getId(),
              recipient.getEmail(),
              "Password reset request",
              String.format(
                  "We've got the password reset request for your account at %s.\n\nYou can continue password reset by visiting this page: %s",
                  event.createdAt(), redirectUrl));
      var notification = notificationService.create(command);

      notificationService.send(notification, channels);
    } catch (Exception ex) {
      log.error("Failed to send password reset email", ex);
    }
  }

  private String getFrontendPasswordResetUrl(PasswordResetRequestEvent event) {
    return UriComponentsBuilder.fromUriString(passwordResetProperties.frontendUrl())
        .queryParam("token", event.passwordResetToken())
        .build()
        .toString();
  }
}
