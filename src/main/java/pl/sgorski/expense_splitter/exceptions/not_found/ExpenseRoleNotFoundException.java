package pl.sgorski.expense_splitter.exceptions.not_found;

public final class ExpenseRoleNotFoundException extends NotFoundException {
  public ExpenseRoleNotFoundException(String role) {
    super("Expense role not found: " + role);
  }
}
