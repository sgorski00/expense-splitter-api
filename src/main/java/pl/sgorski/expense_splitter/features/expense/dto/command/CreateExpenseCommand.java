package pl.sgorski.expense_splitter.features.expense.dto.command;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import pl.sgorski.expense_splitter.features.expense.domain.SplitType;

public record CreateExpenseCommand(
    String title,
    String description,
    BigDecimal amount,
    SplitType splitType,
    Set<ParticipantCommand> participants,
    Instant expenseDate) {}
