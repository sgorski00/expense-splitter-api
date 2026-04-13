package pl.sgorski.expense_splitter.notification.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.notification.domain.UserNotificationPreference;
import pl.sgorski.expense_splitter.notification.dto.NotificationChannel;
import pl.sgorski.expense_splitter.notification.repository.UserNotificationPreferenceRepository;

@ExtendWith(MockitoExtension.class)
public class NotificationPreferenceServiceTest {

  @Mock private UserNotificationPreferenceRepository preferenceRepository;
  @InjectMocks private NotificationPreferenceService notificationPreferenceService;
  private User user;
  private UserNotificationPreference preference;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail("test@example.com");
    preference = new UserNotificationPreference();
    preference.setId(UUID.randomUUID());
    preference.setUser(user);
    preference.setEmailNotificationsEnabled(true);
    preference.setWebsocketNotificationsEnabled(true);
  }

  @Test
  void getNotificationChannelsForUser_shouldReturnBothChannels_whenBothEnabled() {
    when(preferenceRepository.findByUser(user)).thenReturn(Optional.of(preference));

    var result = notificationPreferenceService.getNotificationChannelsForUser(user);

    assertEquals(2, result.size());
    assertTrue(result.contains(NotificationChannel.EMAIL));
    assertTrue(result.contains(NotificationChannel.WEBSOCKET));
    verify(preferenceRepository, times(1)).findByUser(user);
  }

  @Test
  void getNotificationChannelsForUser_shouldReturnOnlyEmailChannel_whenOnlyEmailEnabled() {
    preference.setWebsocketNotificationsEnabled(false);
    when(preferenceRepository.findByUser(user)).thenReturn(Optional.of(preference));

    var result = notificationPreferenceService.getNotificationChannelsForUser(user);

    assertEquals(1, result.size());
    assertTrue(result.contains(NotificationChannel.EMAIL));
    assertFalse(result.contains(NotificationChannel.WEBSOCKET));
  }

  @Test
  void getNotificationChannelsForUser_shouldReturnOnlyWebsocketChannel_whenOnlyWebsocketEnabled() {
    preference.setEmailNotificationsEnabled(false);
    when(preferenceRepository.findByUser(user)).thenReturn(Optional.of(preference));

    var result = notificationPreferenceService.getNotificationChannelsForUser(user);

    assertEquals(1, result.size());
    assertTrue(result.contains(NotificationChannel.WEBSOCKET));
    assertFalse(result.contains(NotificationChannel.EMAIL));
  }

  @Test
  void getNotificationChannelsForUser_shouldReturnEmptySet_whenBothDisabled() {
    preference.setEmailNotificationsEnabled(false);
    preference.setWebsocketNotificationsEnabled(false);
    when(preferenceRepository.findByUser(user)).thenReturn(Optional.of(preference));

    var result = notificationPreferenceService.getNotificationChannelsForUser(user);

    assertTrue(result.isEmpty());
  }

  @Test
  void getNotificationChannelsForUser_shouldReturnEmptySet_whenPreferenceNotFound() {
    when(preferenceRepository.findByUser(user)).thenReturn(Optional.empty());

    var result = notificationPreferenceService.getNotificationChannelsForUser(user);

    assertTrue(result.isEmpty());
    verify(preferenceRepository, times(1)).findByUser(user);
  }
}
