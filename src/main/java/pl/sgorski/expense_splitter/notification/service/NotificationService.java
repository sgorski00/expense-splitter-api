package pl.sgorski.expense_splitter.notification.service;

import pl.sgorski.expense_splitter.notification.dto.NotificationCommand;

public interface NotificationService {
  void send(NotificationCommand command);
}
