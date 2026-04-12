package pl.sgorski.expense_splitter.notification.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.notification.dto.NotificationCommand;
import pl.sgorski.expense_splitter.notification.service.NotificationService;
import pl.sgorski.expense_splitter.notification.service.channel.NotificationSender;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  private final List<NotificationSender> senders;

  @Override
  public void send(NotificationCommand command) {
    command
        .channels()
        .forEach(
            channel ->
                senders.stream()
                    .filter(sender -> sender.supports(channel))
                    .forEach(sender -> sender.send(command)));
  }
}
