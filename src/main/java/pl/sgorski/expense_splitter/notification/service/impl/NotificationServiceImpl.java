package pl.sgorski.expense_splitter.notification.service.impl;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.exceptions.notification.NotificationNotFoundException;
import pl.sgorski.expense_splitter.features.user.service.UserService;
import pl.sgorski.expense_splitter.notification.domain.Notification;
import pl.sgorski.expense_splitter.notification.dto.NotificationCommand;
import pl.sgorski.expense_splitter.notification.repository.NotificationRepository;
import pl.sgorski.expense_splitter.notification.service.NotificationService;
import pl.sgorski.expense_splitter.notification.service.channel.NotificationSender;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  private final List<NotificationSender> senders;
  private final NotificationRepository repository;
  private final UserService userService;

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

  @Transactional
  public Notification create(@Valid NotificationCommand command) {
    var user = userService.getUser(command.userId());
    var notification = new Notification();
    notification.setUser(user);
    notification.setTitle(command.title());
    notification.setBody(command.content());
    notification.setRead(false);
    return repository.save(notification);
  }

  @Transactional
  public Notification markAsRead(@NotNull UUID id) {
    var notification = getNotification(id);
    notification.setRead(true);
    return repository.save(notification);
  }

  public Notification getNotification(UUID id) {
    return repository.findById(id).orElseThrow(() -> new NotificationNotFoundException(id));
  }
}
