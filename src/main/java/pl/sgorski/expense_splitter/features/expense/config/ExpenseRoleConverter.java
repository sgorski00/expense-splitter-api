package pl.sgorski.expense_splitter.features.expense.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pl.sgorski.expense_splitter.features.expense.dto.filter.ExpenseRole;

@Component
public class ExpenseRoleConverter implements Converter<String, ExpenseRole> {
  @Override
  public ExpenseRole convert(String source) {
    return ExpenseRole.fromString(source);
  }
}
