package pl.sgorski.expense_splitter.features.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.expense_splitter.exceptions.user.IdentityNotFoundException;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.domain.UserIdentity;
import pl.sgorski.expense_splitter.features.user.repository.UserIdentityRepository;

@ExtendWith(MockitoExtension.class)
public class UserIdentityServiceTest {

  @Mock private UserIdentityRepository userIdentityRepository;
  @InjectMocks private UserIdentityService userIdentityService;

  private final String providerId = "providerId";

  @Test
  void isUserIdentityPresent_shouldReturnTrue_whenIdentityExists() {
    when(userIdentityRepository.existsByProviderAndProviderId(
            any(AuthProvider.class), eq(providerId)))
        .thenReturn(true);

    var exists = userIdentityService.isUserIdentityPresent(providerId, AuthProvider.GOOGLE);

    assertTrue(exists);
    verify(userIdentityRepository, times(1))
        .existsByProviderAndProviderId(any(AuthProvider.class), eq(providerId));
    verifyNoMoreInteractions(userIdentityRepository);
  }

  @Test
  void isUserIdentityPresent_shouldReturnFalse_whenIdentityDoesNotExist() {
    when(userIdentityRepository.existsByProviderAndProviderId(
            any(AuthProvider.class), eq(providerId)))
        .thenReturn(false);

    var exists = userIdentityService.isUserIdentityPresent(providerId, AuthProvider.GOOGLE);

    assertFalse(exists);
    verify(userIdentityRepository, times(1))
        .existsByProviderAndProviderId(any(AuthProvider.class), eq(providerId));
    verifyNoMoreInteractions(userIdentityRepository);
  }

  @Test
  void findIdentity_shouldReturnUserIdentity_whenIdentityExists() {
    var identity = new UserIdentity();
    when(userIdentityRepository.findWithUserByProviderAndProviderId(
            any(AuthProvider.class), eq(providerId)))
        .thenReturn(Optional.of(identity));

    var found = userIdentityService.findIdentity(AuthProvider.GOOGLE, providerId);

    assertNotNull(found);
    verify(userIdentityRepository, times(1))
        .findWithUserByProviderAndProviderId(any(AuthProvider.class), eq(providerId));
    verifyNoMoreInteractions(userIdentityRepository);
  }

  @Test
  void findIdentity_shouldThrowIdentityNoTFound_whenIdentityDontExists() {
    when(userIdentityRepository.findWithUserByProviderAndProviderId(
            any(AuthProvider.class), eq(providerId)))
        .thenReturn(Optional.empty());

    assertThrows(
        IdentityNotFoundException.class,
        () -> userIdentityService.findIdentity(AuthProvider.GOOGLE, providerId));
    verify(userIdentityRepository, times(1))
        .findWithUserByProviderAndProviderId(any(AuthProvider.class), eq(providerId));
    verifyNoMoreInteractions(userIdentityRepository);
  }

  @Test
  void removeAllUserIdentities_shouldDeleteAllIdentitiesForUser() {
    var user = new User();

    userIdentityService.removeAllUserIdentities(user);

    verify(userIdentityRepository, times(1)).deleteAllByUser(eq(user));
    verifyNoMoreInteractions(userIdentityRepository);
  }
}
