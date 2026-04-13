package pl.sgorski.expense_splitter.notification.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.exceptions.notification.NotificationNotFoundException;
import pl.sgorski.expense_splitter.features.user.service.UserService;
import pl.sgorski.expense_splitter.notification.domain.Notification;
import pl.sgorski.expense_splitter.notification.dto.NotificationCommand;
import pl.sgorski.expense_splitter.notification.repository.NotificationRepository;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final List<NotificationSender> senders;
  private final NotificationRepository repository;
  private final UserService userService;

  public void send(@Valid NotificationCommand command) {
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
  public Notification markAsRead(UUID id) {
    var notification = getNotification(id);
    notification.setRead(true);
    return repository.save(notification);
  }

  public Notification getNotification(UUID id) {
    return repository.findById(id).orElseThrow(() -> new NotificationNotFoundException(id));
  }
}
