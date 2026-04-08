package pl.sgorski.expense_splitter.exceptions.not_found;

import java.util.UUID;

public final class PaymentNotFoundException extends NotFoundException {

  public PaymentNotFoundException(UUID id) {
    super("Payment not found with id: " + id.toString());
  }
}
