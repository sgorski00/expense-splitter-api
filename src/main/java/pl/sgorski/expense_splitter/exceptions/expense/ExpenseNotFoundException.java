package pl.sgorski.expense_splitter.exceptions.expense;

import java.util.UUID;
import pl.sgorski.expense_splitter.exceptions.NotFoundException;

public final class ExpenseNotFoundException extends NotFoundException {

  public ExpenseNotFoundException(UUID id) {
    super("Expense not found with id: " + id.toString());
  }
}
