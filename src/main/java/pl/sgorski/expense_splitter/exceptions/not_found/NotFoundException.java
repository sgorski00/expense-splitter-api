package pl.sgorski.expense_splitter.exceptions.not_found;

public class NotFoundException extends RuntimeException {
  public NotFoundException(String message) {
    super(message);
  }
}
