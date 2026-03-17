package pl.sgorski.expense_splitter.features.expense.dto.response;

import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record DetailedExpenseResponse(
        Long id,
        String title,
        @Nullable String description,
        BigDecimal amountTotal,
        BigDecimal myBalance,
        List<ExpenseParticipantResponse> participants,
        Instant expenseDate,
        Instant createdAt,
        Instant updatedAt,
        @Nullable Instant deletedAt
) { }
