package pl.sgorski.expense_splitter.exceptions.authentication;

import org.springframework.security.core.AuthenticationException;

public final class FailedLoginAttemptException extends AuthenticationException {
  public FailedLoginAttemptException(String message) {
    super(message);
  }
}
