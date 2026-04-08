package pl.sgorski.expense_splitter.exceptions.payment;

import pl.sgorski.expense_splitter.exceptions.NotFoundException;

import java.util.UUID;

public final class PaymentNotFoundException extends NotFoundException {

  public PaymentNotFoundException(UUID id) {
    super("Payment not found with id: " + id.toString());
  }
}
