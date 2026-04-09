package pl.sgorski.expense_splitter.exceptions.payment;

import java.util.UUID;
import pl.sgorski.expense_splitter.exceptions.NotFoundException;

public final class PaymentNotFoundException extends NotFoundException {

  public PaymentNotFoundException(UUID id) {
    super("Payment not found with id: " + id.toString());
  }
}
