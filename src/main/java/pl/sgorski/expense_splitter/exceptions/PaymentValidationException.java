package pl.sgorski.expense_splitter.exceptions;

public class PaymentValidationException extends RuntimeException {
  public PaymentValidationException(String message) {
    super(message);
  }
}
