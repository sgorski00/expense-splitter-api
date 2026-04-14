package pl.sgorski.expense_splitter.notification.service;

import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.exceptions.notification.NotificationPreferenceNotFoundException;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.notification.domain.NotificationChannel;
import pl.sgorski.expense_splitter.notification.domain.UserNotificationPreference;
import pl.sgorski.expense_splitter.notification.dto.command.UpdateNotificationRequestCommand;
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

  @Transactional
  public UserNotificationPreference updatePreferences(
      User user, UpdateNotificationRequestCommand command) {
    var preferences =
        preferenceRepository.findByUser(user).orElse(new UserNotificationPreference());
    preferences.setUser(user);
    if (command.emailNotificationsEnabled() != null) {
      preferences.setEmailNotificationsEnabled(command.emailNotificationsEnabled());
    }
    if (command.websocketNotificationsEnabled() != null) {
      preferences.setWebsocketNotificationsEnabled(command.websocketNotificationsEnabled());
    }

    return preferenceRepository.save(preferences);
  }

  public UserNotificationPreference getPreferencesForUser(User user) {
    return preferenceRepository
        .findByUser(user)
        .orElseThrow(() -> new NotificationPreferenceNotFoundException(user.getId()));
  }
}
