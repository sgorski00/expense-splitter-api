package pl.sgorski.expense_splitter.notification.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.exceptions.notification.NotificationNotFoundException;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.service.UserService;
import pl.sgorski.expense_splitter.notification.domain.Notification;
import pl.sgorski.expense_splitter.notification.domain.NotificationChannel;
import pl.sgorski.expense_splitter.notification.dto.command.NotificationCommand;
import pl.sgorski.expense_splitter.notification.repository.NotificationRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

  private final List<NotificationSender> senders;
  private final NotificationRepository repository;
  private final UserService userService;

  public void send(Notification notification, Set<NotificationChannel> channels) {
    log.debug(
        "Sending notification {} to user {} via channels: {}",
        notification.getId(),
        notification.getUser().getId(),
        channels);
    channels.forEach(
        channel ->
            senders.stream()
                .filter(sender -> sender.supports(channel))
                .forEach(sender -> sender.send(notification)));
  }

  @Transactional
  public Notification create(@Valid NotificationCommand command) {
    var user = userService.getUser(command.userId());
    var notification = new Notification();
    notification.setUser(user);
    notification.setTitle(command.title());
    notification.setBody(command.content());
    notification.setIsRead(false);
    return repository.save(notification);
  }

  @Transactional
  public Notification markAsRead(UUID id, User user) {
    var notification =
        repository
            .findById(id)
            .filter(n -> n.getUser().equals(user))
            .orElseThrow(() -> new NotificationNotFoundException(id));
    notification.setIsRead(true);
    return repository.save(notification);
  }

  public Page<Notification> getUnread(UUID userId, Pageable pageable) {
    return repository.findAllByUserIdAndIsReadOrderByCreatedAtDesc(userId, true, pageable);
  }
}
