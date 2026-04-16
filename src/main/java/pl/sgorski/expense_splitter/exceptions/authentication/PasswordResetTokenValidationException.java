package pl.sgorski.expense_splitter.exceptions.authentication;

/** Thrown when a password reset token is invalid - e.g. expired, malformed, revoked. */
public final class PasswordResetTokenValidationException extends RuntimeException {
  public PasswordResetTokenValidationException(String message) {
    super(message);
  }
}
