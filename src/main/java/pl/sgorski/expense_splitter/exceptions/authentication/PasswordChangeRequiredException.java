package pl.sgorski.expense_splitter.exceptions.authentication;

/**
 * Thrown when account is marked as password changed required and trying to visit endpoints that are
 * not whitelisted
 */
public final class PasswordChangeRequiredException extends RuntimeException {
  public PasswordChangeRequiredException() {
    super("Before you can log in, you need to change your password.");
  }

  public PasswordChangeRequiredException(String message) {
    super(message);
  }
}
