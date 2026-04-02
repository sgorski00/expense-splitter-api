package pl.sgorski.expense_splitter.exceptions;

/** Occurs when user provides wrong current password during password change. */
public final class InvalidPasswordException extends RuntimeException {
  public InvalidPasswordException(String message) {
    super(message);
  }
}
