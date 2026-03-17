package pl.sgorski.expense_splitter.features.expense.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record ExpenseResponse(
        Long id,
        String title,
        BigDecimal amountTotal,
        BigDecimal myBalance,
        Instant expenseDate
) { }
