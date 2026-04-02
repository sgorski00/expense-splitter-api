package pl.sgorski.expense_splitter.exceptions;

public final class InvalidEmailException extends RuntimeException {
  public InvalidEmailException(String email) {
    super("Enter email is not valid: " + email);
  }
}
