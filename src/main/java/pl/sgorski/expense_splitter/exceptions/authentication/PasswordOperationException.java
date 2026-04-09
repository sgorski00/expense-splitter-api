package pl.sgorski.expense_splitter.exceptions.authentication;

/**
 * Thrown when an operation on user password fails due to invalid state. <br>
 * Common cases: - User already has a local password (when setting password for OAuth2 user) - User
 * doesn't have a local password (when changing password)
 */
public final class PasswordOperationException extends RuntimeException {
  public PasswordOperationException(String message) {
    super(message);
  }
}
