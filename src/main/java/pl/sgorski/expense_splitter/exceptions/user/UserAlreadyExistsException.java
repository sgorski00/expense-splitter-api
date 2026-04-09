package pl.sgorski.expense_splitter.exceptions.user;

/** Exception thrown when trying to create a user that already exists in the system. */
public final class UserAlreadyExistsException extends RuntimeException {
  public UserAlreadyExistsException() {
    super("Given user already exists");
  }
}
