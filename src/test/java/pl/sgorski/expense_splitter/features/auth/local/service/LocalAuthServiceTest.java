package pl.sgorski.expense_splitter.features.auth.local.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.sgorski.expense_splitter.exceptions.authentication.InvalidPasswordException;
import pl.sgorski.expense_splitter.exceptions.authentication.PasswordOperationException;
import pl.sgorski.expense_splitter.exceptions.user.UserAlreadyExistsException;
import pl.sgorski.expense_splitter.exceptions.user.UserNotFoundException;
import pl.sgorski.expense_splitter.features.auth.dto.command.ConfirmPasswordResetCommand;
import pl.sgorski.expense_splitter.features.auth.dto.command.LoginUserCommand;
import pl.sgorski.expense_splitter.features.auth.dto.command.RegisterUserCommand;
import pl.sgorski.expense_splitter.features.auth.mapper.AuthMapper;
import pl.sgorski.expense_splitter.features.auth.password_reset_token.domain.PasswordResetToken;
import pl.sgorski.expense_splitter.features.auth.password_reset_token.event.PasswordResetRequestEvent;
import pl.sgorski.expense_splitter.features.auth.password_reset_token.service.PasswordResetTokenService;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class LocalAuthServiceTest {

  @Mock private UserService userService;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private AuthenticationManager authenticationManager;
  @Mock private PasswordResetTokenService passwordResetTokenService;
  @Mock private ApplicationEventPublisher eventPublisher;

  private LocalAuthService localAuthService;

  private final String rawPassword = "P@ssword123";
  private final String encodedPassword = "EncodedP@ssword123";
  private final String email = "user@example.com";
  private RegisterUserCommand registerCommand;
  private LoginUserCommand loginCommand;

  @BeforeEach
  void setUp() {
    registerCommand = new RegisterUserCommand(email, "John", "Doe", rawPassword, rawPassword);
    loginCommand = new LoginUserCommand(email, rawPassword);
    var authMapper = Mappers.getMapper(AuthMapper.class);
    localAuthService =
        new LocalAuthService(
            userService,
            authMapper,
            passwordEncoder,
            authenticationManager,
            passwordResetTokenService,
            eventPublisher);
  }

  @Test
  void registerUser_shouldCreateUser_whenEmailNotExists() {
    when(userService.isUserPresent(eq(email))).thenReturn(false);
    when(passwordEncoder.encode(eq(rawPassword))).thenReturn(encodedPassword);
    when(userService.save(any(User.class))).thenReturn(new User());

    var user = localAuthService.registerUser(registerCommand);

    assertNotNull(user);
    verify(userService, times(1)).isUserPresent(eq(email));
    verify(userService, times(1)).save(any(User.class));
    verify(passwordEncoder, times(1)).encode(eq(rawPassword));
  }

  @Test
  void registerUser_shouldThrowException_whenUserAlreadyExists() {
    when(userService.isUserPresent(eq(email))).thenReturn(true);

    assertThrows(
        UserAlreadyExistsException.class, () -> localAuthService.registerUser(registerCommand));
    verify(userService, times(1)).isUserPresent(eq(email));
    verify(userService, never()).save(any(User.class));
  }

  @Test
  void login_shouldReturnUser_whenCredentialsAreValid() {
    var userId = UUID.randomUUID();
    var principal = new User();
    principal.setId(userId);
    var authentication = new UsernamePasswordAuthenticationToken(principal, null, List.of());
    when(authenticationManager.authenticate(any())).thenReturn(authentication);
    when(userService.getUser(eq(userId))).thenReturn(principal);

    var user = localAuthService.login(loginCommand);

    assertNotNull(user);
    verify(authenticationManager, times(1)).authenticate(any());
    verify(userService, times(1)).getUser(eq(userId));
  }

  @Test
  void login_shouldThrowException_whenAuthenticationFails() {
    when(authenticationManager.authenticate(any()))
        .thenThrow(new RuntimeException("Authentication failed"));

    assertThrows(RuntimeException.class, () -> localAuthService.login(loginCommand));

    verify(authenticationManager, times(1)).authenticate(any());
    verifyNoInteractions(userService);
  }

  @Test
  void login_shouldThrowException_whenPrincipalIsNull() {
    var authentication = new UsernamePasswordAuthenticationToken(null, null, List.of());
    when(authenticationManager.authenticate(any())).thenReturn(authentication);

    var exception =
        assertThrows(NullPointerException.class, () -> localAuthService.login(loginCommand));

    assertEquals("Authentication failed", exception.getMessage());
    verify(authenticationManager, times(1)).authenticate(any());
    verifyNoInteractions(userService);
  }

  @Test
  void setLocalPassword_shouldSetPassword_whenUserHasNoPassword() {
    var user = new User();
    user.setPasswordHash(null);
    when(passwordEncoder.encode(eq(rawPassword))).thenReturn(encodedPassword);

    localAuthService.setLocalPassword(user, rawPassword);

    verify(userService, times(1)).save(eq(user));
    verify(passwordEncoder, times(1)).encode(eq(rawPassword));
    assertFalse(user.isPasswordForChange());
    assertNotNull(user.getPasswordHash());
  }

  @Test
  void setLocalPassword_shouldThrowException_whenUserAlreadyHasPassword() {
    var user = new User();
    user.setPasswordHash(encodedPassword);

    assertThrows(
        PasswordOperationException.class,
        () -> localAuthService.setLocalPassword(user, rawPassword));
    verifyNoInteractions(userService);
    verifyNoInteractions(passwordEncoder);
    assertEquals(encodedPassword, user.getPasswordHash());
  }

  @Test
  void changePassword_shouldUpdatePassword_whenOldPasswordIsValid() {
    var oldPassword = "OldP@ssword123";
    var newEncodedPassword = "NewEncodedP@ssword123";
    var user = new User();
    user.setPasswordHash(encodedPassword);
    when(passwordEncoder.matches(eq(oldPassword), eq(encodedPassword))).thenReturn(true);
    when(passwordEncoder.encode(eq(rawPassword))).thenReturn(newEncodedPassword);

    localAuthService.changePassword(user, oldPassword, rawPassword);

    verify(userService, times(1)).save(eq(user));
    verify(passwordEncoder, times(1)).matches(eq(oldPassword), eq(encodedPassword));
    verify(passwordEncoder, times(1)).encode(eq(rawPassword));
    assertFalse(user.isPasswordForChange());
    assertEquals(newEncodedPassword, user.getPasswordHash());
  }

  @Test
  void changePassword_shouldThrowException_whenUserHasNoPassword() {
    var oldPassword = "OldP@ssword123";
    var user = new User();
    user.setPasswordHash(null);

    assertThrows(
        PasswordOperationException.class,
        () -> localAuthService.changePassword(user, oldPassword, rawPassword));
    verifyNoInteractions(userService);
    verifyNoInteractions(passwordEncoder);
    assertNull(user.getPasswordHash());
  }

  @Test
  void changePassword_shouldThrowException_whenOldPasswordIsInvalid() {
    var oldPassword = "OldP@ssword123";
    var user = new User();
    user.setPasswordHash(encodedPassword);
    when(passwordEncoder.matches(eq(oldPassword), eq(encodedPassword))).thenReturn(false);

    assertThrows(
        InvalidPasswordException.class,
        () -> localAuthService.changePassword(user, oldPassword, rawPassword));
    verifyNoInteractions(userService);
    verify(passwordEncoder, never()).encode(anyString());
    assertEquals(encodedPassword, user.getPasswordHash());
  }

  @Test
  void requestPasswordReset_shouldGenerateTokenAndPublishEvent_whenUserExists() {
    var userId = UUID.randomUUID();
    var user = new User();
    user.setId(userId);
    var tokenUUID = UUID.randomUUID();
    var resetToken = new PasswordResetToken();
    resetToken.setToken(tokenUUID);
    resetToken.setUser(user);
    when(userService.getUser(eq(email))).thenReturn(user);
    when(passwordResetTokenService.generatePasswordResetToken(eq(user))).thenReturn(resetToken);

    localAuthService.requestPasswordReset(email);

    verify(userService, times(1)).getUser(eq(email));
    verify(passwordResetTokenService, times(1)).generatePasswordResetToken(eq(user));
    verify(eventPublisher, times(1)).publishEvent(any(PasswordResetRequestEvent.class));
  }

  @Test
  void requestPasswordReset_shouldNotThrow_whenUserNotFound() {
    when(userService.getUser(eq(email))).thenThrow(new UserNotFoundException(email));

    assertDoesNotThrow(() -> localAuthService.requestPasswordReset(email));

    verify(userService, times(1)).getUser(eq(email));
    verify(passwordResetTokenService, never()).generatePasswordResetToken(any());
    verify(eventPublisher, never()).publishEvent(any());
  }

  @Test
  void resetPassword_shouldUpdatePasswordAndRevokeTokens_whenTokenIsValid() {
    var userId = UUID.randomUUID();
    var user = new User();
    user.setId(userId);
    var tokenUUID = UUID.randomUUID();
    var resetToken = new PasswordResetToken();
    resetToken.setUser(user);
    resetToken.setRevoked(false);
    resetToken.setExpiresAt(java.time.Instant.now().plusSeconds(60));
    var newEncodedPassword = "NewEncodedP@ssword123";
    var command = new ConfirmPasswordResetCommand(tokenUUID, rawPassword);
    when(passwordResetTokenService.getToken(eq(tokenUUID))).thenReturn(resetToken);
    when(passwordEncoder.encode(eq(rawPassword))).thenReturn(newEncodedPassword);

    localAuthService.resetPassword(command);

    assertEquals(newEncodedPassword, user.getPasswordHash());
    assertFalse(user.isPasswordForChange());
    verify(passwordResetTokenService, times(1)).getToken(eq(tokenUUID));
    verify(passwordEncoder, times(1)).encode(eq(rawPassword));
    verify(userService, times(1)).save(eq(user));
    verify(passwordResetTokenService, times(1)).revokeAllUserTokens(eq(userId));
  }

  @Test
  void resetPassword_shouldThrowException_whenTokenIsExpired() {
    var tokenUUID = UUID.randomUUID();
    var resetToken = new PasswordResetToken();
    resetToken.setRevoked(false);
    resetToken.setExpiresAt(java.time.Instant.now().minusSeconds(60));
    var command = new ConfirmPasswordResetCommand(tokenUUID, rawPassword);
    when(passwordResetTokenService.getToken(eq(tokenUUID))).thenReturn(resetToken);

    assertThrows(RuntimeException.class, () -> localAuthService.resetPassword(command));

    verify(passwordResetTokenService, times(1)).getToken(eq(tokenUUID));
    verifyNoInteractions(passwordEncoder, userService);
    verify(passwordResetTokenService, never()).revokeAllUserTokens(any());
  }

  @Test
  void resetPassword_shouldThrowException_whenTokenIsRevoked() {
    var tokenUUID = UUID.randomUUID();
    var resetToken = new PasswordResetToken();
    resetToken.setRevoked(true);
    resetToken.setExpiresAt(java.time.Instant.now().plusSeconds(60));
    var command = new ConfirmPasswordResetCommand(tokenUUID, rawPassword);
    when(passwordResetTokenService.getToken(eq(tokenUUID))).thenReturn(resetToken);

    assertThrows(RuntimeException.class, () -> localAuthService.resetPassword(command));

    verify(passwordResetTokenService, times(1)).getToken(eq(tokenUUID));
    verifyNoInteractions(passwordEncoder, userService);
    verify(passwordResetTokenService, never()).revokeAllUserTokens(any());
  }
}
