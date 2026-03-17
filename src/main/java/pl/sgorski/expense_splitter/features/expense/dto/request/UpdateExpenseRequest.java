package pl.sgorski.expense_splitter.features.expense.dto.request;

import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;

public record UpdateExpenseRequest(
        @Nullable String title,
        @Nullable String description,
        @Nullable BigDecimal amount
) { }
