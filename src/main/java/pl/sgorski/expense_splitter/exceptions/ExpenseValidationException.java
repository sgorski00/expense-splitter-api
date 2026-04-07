package pl.sgorski.expense_splitter.exceptions;

public class ExpenseValidationException extends RuntimeException {
  public ExpenseValidationException(String message) {
    super(message);
  }
}
