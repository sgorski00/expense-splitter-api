package pl.sgorski.expense_splitter.features.expense.service.split.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sgorski.expense_splitter.features.expense.domain.ExpenseShare;
import pl.sgorski.expense_splitter.features.expense.dto.command.ParticipantCommand;
import pl.sgorski.expense_splitter.features.expense.service.split.SplitStrategy;
import pl.sgorski.expense_splitter.features.user.service.UserService;

@Component
@RequiredArgsConstructor
public final class EqualSplitStrategy implements SplitStrategy {

  private final UserService userService;

  @Override
  public Set<ExpenseShare> split(BigDecimal amount, Set<ParticipantCommand> participants) {
    var numberOfParticipants = participants.size();
    var shareAmount =
        amount.divide(BigDecimal.valueOf(numberOfParticipants), 2, RoundingMode.HALF_UP);
    var userIds = participants.stream().map(ParticipantCommand::userId).collect(Collectors.toSet());
    var users = userService.getUsers(userIds);
    return users.stream()
        .map(
            user -> {
              var share = new ExpenseShare();
              share.setUser(user);
              share.setAmount(shareAmount);
              return share;
            })
        .collect(Collectors.toSet());
  }
}
