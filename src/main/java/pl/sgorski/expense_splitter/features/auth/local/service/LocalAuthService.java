package pl.sgorski.expense_splitter.features.auth.local.service;

import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.exceptions.authentication.FailedLoginAttemptException;
import pl.sgorski.expense_splitter.exceptions.authentication.InvalidPasswordException;
import pl.sgorski.expense_splitter.exceptions.authentication.PasswordOperationException;
import pl.sgorski.expense_splitter.exceptions.user.UserAlreadyExistsException;
import pl.sgorski.expense_splitter.exceptions.user.UserNotFoundException;
import pl.sgorski.expense_splitter.features.auth.dto.command.ConfirmPasswordResetCommand;
import pl.sgorski.expense_splitter.features.auth.dto.command.LoginUserCommand;
import pl.sgorski.expense_splitter.features.auth.dto.command.RegisterUserCommand;
import pl.sgorski.expense_splitter.features.auth.event.FailedLoginAttemptEvent;
import pl.sgorski.expense_splitter.features.auth.mapper.AuthMapper;
import pl.sgorski.expense_splitter.features.auth.password_reset_token.event.PasswordResetRequestEvent;
import pl.sgorski.expense_splitter.features.auth.password_reset_token.service.PasswordResetTokenService;
import pl.sgorski.expense_splitter.features.auth.refresh_token.service.RefreshTokenService;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.service.UserService;

@Service
@RequiredArgsConstructor
public class LocalAuthService {

  private final UserService userService;
  private final AuthMapper authMapper;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final PasswordResetTokenService passwordResetTokenService;
  private final ApplicationEventPublisher eventPublisher;
  private final RefreshTokenService refreshTokenService;

  @Transactional
  public User registerUser(RegisterUserCommand command) {
    if (userService.isUserPresent(command.email())) {
      throw new UserAlreadyExistsException();
    }

    var user = authMapper.toEntity(command);
    user.setPasswordHash(passwordEncoder.encode(command.newPassword()));
    return userService.save(user);
  }

  /** Validate user's login request and returns user if credentials are correct */
  public User login(LoginUserCommand command) {
    try {
      var auth =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(command.email(), command.password()));
      var principal = Objects.requireNonNull(auth.getPrincipal(), "Authentication failed");
      var user = (User) principal;
      return userService.getUser(user.getId());
    } catch (BadCredentialsException e) {
      var failedLoginEvent = new FailedLoginAttemptEvent(command.email(), Instant.now());
      eventPublisher.publishEvent(failedLoginEvent);
      throw new FailedLoginAttemptException("Invalid email or password.");
    }
  }

  /** Method that allows oauth2 users to create local password and login with email and password. */
  @Transactional
  public void setLocalPassword(User user, String rawPassword) {
    if (user.getPasswordHash() != null) {
      throw new PasswordOperationException(
          "User already has a password. If you want to change it, use change password then.");
    }
    user.setPasswordHash(passwordEncoder.encode(rawPassword));
    user.setPasswordForChange(false);
    userService.save(user);
  }

  @Transactional
  public void changePassword(User user, String oldPassword, String newPassword) {
    if (user.getPasswordHash() == null) {
      throw new PasswordOperationException("User doesn't have local password yet.");
    }

    if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
      throw new InvalidPasswordException("Invalid current password.");
    }

    user.setPasswordHash(passwordEncoder.encode(newPassword));
    user.setPasswordForChange(false);
    userService.save(user);
    refreshTokenService.revokeAllUserTokens(user.getId());
  }

  @Transactional
  public void requestPasswordReset(String email) {
    try {
      var user = userService.getUser(email);
      generateToken(user);
    } catch (UserNotFoundException ignored) {
      // for security reasons I don't want to reveal if user with given email exists or not
    }
  }

  @Transactional
  public void requestPasswordResetAndThrowsWhenUserNotFound(UUID id) {
    var user = userService.getUser(id);
    generateToken(user);
  }

  @Transactional
  public void resetPassword(ConfirmPasswordResetCommand command) {
    var resetToken = passwordResetTokenService.getValidToken(command.token());
    var user = resetToken.getUser();
    user.setPasswordHash(passwordEncoder.encode(command.newPassword()));
    user.setPasswordForChange(false);
    userService.save(user);
    passwordResetTokenService.revokeAllUserTokens(user.getId());
    refreshTokenService.revokeAllUserTokens(user.getId());
  }

  private void generateToken(User user) {
    var token = passwordResetTokenService.generatePasswordResetToken(user);
    var passwordResetEvent =
        new PasswordResetRequestEvent(token.getToken(), Instant.now(), token.getUser().getId());
    eventPublisher.publishEvent(passwordResetEvent);
  }
}
