package pl.sgorski.expense_splitter.exceptions.expense;

import pl.sgorski.expense_splitter.exceptions.NotFoundException;

public final class ExpenseRoleNotFoundException extends NotFoundException {
  public ExpenseRoleNotFoundException(String role) {
    super("Expense role not found: " + role);
  }
}
