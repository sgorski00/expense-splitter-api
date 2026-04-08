package pl.sgorski.expense_splitter.exceptions.expense;

import pl.sgorski.expense_splitter.exceptions.DomainObjectValidationException;

public final class ExpenseValidationException extends DomainObjectValidationException {
  public ExpenseValidationException(String message) {
    super(message);
  }
}
