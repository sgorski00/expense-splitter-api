package pl.sgorski.expense_splitter.features.expense.dto.filter;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import pl.sgorski.expense_splitter.exceptions.not_found.ExpenseRoleNotFoundException;

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
}
