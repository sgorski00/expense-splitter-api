package pl.sgorski.expense_splitter.features.expense.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ExpenseParticipantRequest(
        @NotNull Long userId,
        @NotNull @Positive BigDecimal value
) { }
