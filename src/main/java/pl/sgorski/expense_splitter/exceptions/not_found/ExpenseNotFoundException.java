package pl.sgorski.expense_splitter.exceptions.not_found;

import java.util.UUID;

public final class ExpenseNotFoundException extends NotFoundException {

  public ExpenseNotFoundException(UUID id) {
    super("Expense not found with id: " + id.toString());
  }
}
