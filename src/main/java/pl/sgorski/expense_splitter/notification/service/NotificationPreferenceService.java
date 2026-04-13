package pl.sgorski.expense_splitter.notification.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.notification.dto.NotificationChannel;
import pl.sgorski.expense_splitter.notification.repository.UserNotificationPreferenceRepository;

@Service
@RequiredArgsConstructor
public class NotificationPreferenceService {

  private final UserNotificationPreferenceRepository preferenceRepository;

  public Set<NotificationChannel> getNotificationChannelsForUser(User user) {
    return preferenceRepository
        .findByUser(user)
        .map(
            preferences -> {
              var channels = new HashSet<NotificationChannel>();
              if (preferences.getEmailNotificationsEnabled()) {
                channels.add(NotificationChannel.EMAIL);
              }
              if (preferences.getWebsocketNotificationsEnabled()) {
                channels.add(NotificationChannel.WEBSOCKET);
              }
              return Collections.unmodifiableSet(channels);
            })
        .orElse(Collections.emptySet());
  }
}
