package pl.sgorski.expense_splitter.features.expense.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

@Schema(
        name = "Expense Response",
        description = "Basic expense data returned in listings."
)
public record ExpenseResponse(
        @Schema(
                description = "Expense unique identifier.",
                example = "1"
        )
        Long id,
        @Schema(
                description = "Expense title.",
                example = "Dinner in Rome"
        )
        String title,
        @Schema(
                description = "Total expense amount.",
                example = "999.99"
        )
        BigDecimal amountTotal,
        @Schema(
                description = "Amount owed (as a expense participant) or already received (as a expense creator).",
                example = "333.33"
        )
        BigDecimal myBalance,
        @Schema(
                description = "Date and time when expense was incurred.",
                example = "2026-03-18T20:00:00Z"
        )
        Instant expenseDate
) { }
