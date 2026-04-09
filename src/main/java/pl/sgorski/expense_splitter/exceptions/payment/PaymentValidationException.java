package pl.sgorski.expense_splitter.exceptions.payment;

import pl.sgorski.expense_splitter.exceptions.DomainObjectValidationException;

public final class PaymentValidationException extends DomainObjectValidationException {
  public PaymentValidationException(String message) {
    super(message);
  }
}
