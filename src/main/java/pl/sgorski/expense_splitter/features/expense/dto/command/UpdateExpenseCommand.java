package pl.sgorski.expense_splitter.features.expense.dto.command;

import java.math.BigDecimal;
import java.time.Instant;

public record UpdateExpenseCommand(
    String title, String description, BigDecimal amount, Instant expenseDate) {}
