package pl.sgorski.expense_splitter.notification.service;

import pl.sgorski.expense_splitter.notification.domain.Notification;
import pl.sgorski.expense_splitter.notification.dto.NotificationChannel;

public interface NotificationSender {
  boolean supports(NotificationChannel channel);

  void send(Notification notification);
}
