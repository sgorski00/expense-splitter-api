package pl.sgorski.expense_splitter.exceptions;

public final class InvalidEmailException extends RuntimeException {
  public InvalidEmailException(String email) {
    super("Provided email is not valid: " + email);
  }
}
