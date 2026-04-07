package pl.sgorski.expense_splitter.features.expense.service.split;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.features.expense.domain.Expense;
import pl.sgorski.expense_splitter.features.expense.domain.ExpenseShare;
import pl.sgorski.expense_splitter.features.expense.dto.command.ParticipantCommand;

@Service
@RequiredArgsConstructor
public final class ExpenseSplitService {

  private final SplitStrategyFactory splitStrategyFactory;

  public Set<ExpenseShare> split(Expense expense, Set<ParticipantCommand> participantCommands) {
    var splitStrategy = splitStrategyFactory.get(expense.getSplitType());
    return splitStrategy.split(expense.getAmountTotal(), participantCommands);
  }
}
