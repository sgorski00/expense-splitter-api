package pl.sgorski.expense_splitter.notification.service;

import pl.sgorski.expense_splitter.notification.dto.NotificationCommand;

public interface NotificationSender {
  boolean supports(NotificationCommand.Channel channel);

  void send(NotificationCommand command);
}
