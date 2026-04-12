package pl.sgorski.expense_splitter.notification.service.channel.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sgorski.expense_splitter.exceptions.notification.NotificationOperationException;
import pl.sgorski.expense_splitter.notification.dto.NotificationCommand;
import pl.sgorski.expense_splitter.notification.infrastructure.email.EmailSender;
import pl.sgorski.expense_splitter.notification.service.channel.NotificationSender;

@Component
@RequiredArgsConstructor
public class EmailNotificationSender implements NotificationSender {

  private final EmailSender emailSender;

  @Override
  public boolean supports(NotificationCommand.Channel channel) {
    return channel == NotificationCommand.Channel.EMAIL;
  }

  @Override
  public void send(NotificationCommand command) {
    if (command.email() == null) {
      throw new NotificationOperationException("Email address is required to sen email notification");
    }

    emailSender.send(command.email(), command.title(), command.content());
  }
}
