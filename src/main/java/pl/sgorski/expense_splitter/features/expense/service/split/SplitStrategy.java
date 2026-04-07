package pl.sgorski.expense_splitter.features.expense.service.split;

import java.math.BigDecimal;
import java.util.Set;
import pl.sgorski.expense_splitter.features.expense.domain.ExpenseShare;
import pl.sgorski.expense_splitter.features.expense.dto.command.ParticipantCommand;

public interface SplitStrategy {
  Set<ExpenseShare> split(BigDecimal amount, Set<ParticipantCommand> participants);
}
