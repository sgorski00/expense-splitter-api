package pl.sgorski.expense_splitter.features.expense.dto.filter;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import pl.sgorski.expense_splitter.exceptions.not_found.ExpenseRoleNotFoundException;
import pl.sgorski.expense_splitter.features.expense.domain.Expense;
import pl.sgorski.expense_splitter.features.user.domain.User;

public enum ExpenseRole {
  PAYER,
  PARTICIPANT;

  @JsonCreator
  public static ExpenseRole fromString(String value) {
    return Arrays.stream(values())
        .filter(status -> status.name().equalsIgnoreCase(value.trim()))
        .findFirst()
        .orElseThrow(() -> new ExpenseRoleNotFoundException(value));
  }

  public static ExpenseRole fromExpense(User user, Expense expense) {
    return expense.getPayer().equals(user) ? PAYER : PARTICIPANT;
  }
}
