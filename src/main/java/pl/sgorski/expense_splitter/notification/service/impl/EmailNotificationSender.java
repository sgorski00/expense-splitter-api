package pl.sgorski.expense_splitter.notification.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sgorski.expense_splitter.notification.domain.Notification;
import pl.sgorski.expense_splitter.notification.domain.NotificationChannel;
import pl.sgorski.expense_splitter.notification.infrastructure.email.EmailSender;
import pl.sgorski.expense_splitter.notification.service.NotificationSender;

@Component
@RequiredArgsConstructor
public final class EmailNotificationSender implements NotificationSender {

  private final EmailSender emailSender;

  @Override
  public boolean supports(NotificationChannel channel) {
    return channel == NotificationChannel.EMAIL;
  }

  @Override
  public void send(Notification notification) {
    //TODO: map to email message dto and implement email templates
    emailSender.send(
        notification.getUser().getEmail(), notification.getTitle(), notification.getBody());
  }
}
