package pl.sgorski.expense_splitter.features.expense.dto.command;

import java.time.Instant;
import org.jspecify.annotations.Nullable;

public record UpdateExpenseCommand(
    @Nullable String title, @Nullable String description, @Nullable Instant expenseDate) {}
