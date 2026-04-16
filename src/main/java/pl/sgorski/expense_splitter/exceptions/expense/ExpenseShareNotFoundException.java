package pl.sgorski.expense_splitter.exceptions.expense;

import java.util.UUID;
import pl.sgorski.expense_splitter.exceptions.NotFoundException;

public final class ExpenseShareNotFoundException extends NotFoundException {

  public ExpenseShareNotFoundException(UUID expenseId, UUID userId) {
    super(
        String.format(
            "Expense share for user %s not found in the expense with id %s", userId, expenseId));
  }
}
