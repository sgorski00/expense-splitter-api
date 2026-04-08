package pl.sgorski.expense_splitter.exceptions;

public class DomainObjectValidationException extends RuntimeException {
  public DomainObjectValidationException(String message) {
    super(message);
  }
}
