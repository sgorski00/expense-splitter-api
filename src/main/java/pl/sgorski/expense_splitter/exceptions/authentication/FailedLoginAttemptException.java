package pl.sgorski.expense_splitter.exceptions.authentication;

public final class FailedLoginAttemptException extends RuntimeException {
  public FailedLoginAttemptException(String message) {
    super(message);
  }
}
