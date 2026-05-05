package pl.sgorski.expense_splitter.exceptions;

public abstract class NotFoundException extends RuntimeException {
  public NotFoundException(String message) {
    super(message);
  }
}
