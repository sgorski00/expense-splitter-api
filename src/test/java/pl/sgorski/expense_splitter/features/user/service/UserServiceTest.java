package pl.sgorski.expense_splitter.features.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import pl.sgorski.expense_splitter.exceptions.not_found.UserNotFoundException;
import pl.sgorski.expense_splitter.features.auth.refresh_token.service.RefreshTokenService;
import pl.sgorski.expense_splitter.features.user.domain.Role;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private RefreshTokenService refreshTokenService;
  @Mock private UserIdentityService userIdentityService;
  @InjectMocks private UserService userService;

  private final String email = "user@example.com";
  private User user;
  private UUID userId;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    user = new User();
    user.setEmail(email);
    user.setId(userId);
  }

  @Test
  void save_shouldSaveUser_whenUserIsValid() {
    when(userRepository.save(eq(user))).thenReturn(new User());

    var saved = userService.save(user);

    assertNotNull(saved);
    verify(userRepository, times(1)).save(eq(user));
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  void getUserByEmail_shouldReturnUser_whenUserExists() {
    when(userRepository.findByEmailAndDeletedAtIsNull(eq(email))).thenReturn(Optional.of(user));

    var found = userService.getUser(email);

    assertNotNull(found);
    verify(userRepository, times(1)).findByEmailAndDeletedAtIsNull(eq(email));
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  void getUserByEmail_shouldThrowUserNotFound_whenUserNotFound() {
    when(userRepository.findByEmailAndDeletedAtIsNull(eq(email))).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.getUser(email));
    verify(userRepository, times(1)).findByEmailAndDeletedAtIsNull(eq(email));
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  void getUserById_shouldReturnUser_whenUserExists() {
    when(userRepository.findByIdAndDeletedAtIsNull(eq(userId))).thenReturn(Optional.of(user));

    var found = userService.getUser(userId);

    assertNotNull(found);
    verify(userRepository, times(1)).findByIdAndDeletedAtIsNull(eq(userId));
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  void getUserById_shouldThrowUserNotFound_whenUserNotFound() {
    when(userRepository.findByIdAndDeletedAtIsNull(eq(userId))).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.getUser(userId));
    verify(userRepository, times(1)).findByIdAndDeletedAtIsNull(eq(userId));
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  void getUserWithIdentities_shouldReturnUser_whenUserExists() {
    when(userRepository.findWithIdentitiesByIdAndDeletedAtIsNull(eq(userId)))
        .thenReturn(Optional.of(user));

    var found = userService.getUserWithIdentities(userId);

    assertNotNull(found);
    verify(userRepository, times(1)).findWithIdentitiesByIdAndDeletedAtIsNull(eq(userId));
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  void getUserWithIdentities_shouldThrowUserNotFound_whenUserNotFound() {
    when(userRepository.findWithIdentitiesByIdAndDeletedAtIsNull(eq(userId)))
        .thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.getUserWithIdentities(userId));
    verify(userRepository, times(1)).findWithIdentitiesByIdAndDeletedAtIsNull(eq(userId));
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  void isUserPresent_shouldReturnTrue_whenUserExists() {
    when(userRepository.existsByEmailAndDeletedAtIsNull(eq(email))).thenReturn(true);

    var exists = userService.isUserPresent(email);

    assertTrue(exists);
    verify(userRepository, times(1)).existsByEmailAndDeletedAtIsNull(eq(email));
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  void isUserPresent_shouldReturnFalse_whenUserDoesNotExist() {
    when(userRepository.existsByEmailAndDeletedAtIsNull(eq(email))).thenReturn(false);

    var exists = userService.isUserPresent(email);

    assertFalse(exists);
    verify(userRepository, times(1)).existsByEmailAndDeletedAtIsNull(eq(email));
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  void deleteUser_shouldDeleteUserWithIdentitiesAndRevokeTokens_whenUserExists() {
    doNothing().when(refreshTokenService).revokeAllUserTokens(eq(userId));
    doNothing().when(userIdentityService).removeAllUserIdentities(eq(user));
    doNothing().when(userRepository).delete(eq(user));

    userService.deleteUser(user);

    verify(refreshTokenService, times(1)).revokeAllUserTokens(eq(userId));
    verify(userIdentityService, times(1)).removeAllUserIdentities(eq(user));
    verify(userRepository, times(1)).delete(eq(user));
    verifyNoMoreInteractions(refreshTokenService, userIdentityService, userRepository);
  }

  @Test
  void isAdminPresent_shouldReturnTrue_whenAtLeastOneAdmin() {
    when(userRepository.countByRole(eq(Role.ADMIN))).thenReturn(1L);

    var exists = userService.isAdminPresent();

    assertTrue(exists);
    verify(userRepository, times(1)).countByRole(eq(Role.ADMIN));
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  void isAdminPresent_shouldReturnFalse_whenNoAdminIsPresent() {
    when(userRepository.countByRole(eq(Role.ADMIN))).thenReturn(0L);

    var exists = userService.isAdminPresent();

    assertFalse(exists);
    verify(userRepository, times(1)).countByRole(eq(Role.ADMIN));
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  void getUsersByQuery_shouldReturnPageOfUsers() {
    var pageable = Pageable.unpaged();
    var query = "test";

    userService.getUsersByQuery(query, pageable);

    verify(userRepository, times(1)).findByQuery(anyString(), eq(pageable));
    verifyNoMoreInteractions(userRepository);
  }
}
