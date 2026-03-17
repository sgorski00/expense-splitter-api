package pl.sgorski.expense_splitter.features.expense.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

public record CreateExpenseRequest(
        @NotBlank String title,
        @Nullable String description,
        @NotNull @Positive BigDecimal amount,
        @Nullable Set<ExpenseParticipantRequest> participantIds,
        @NotNull Instant expenseDate
) { }
