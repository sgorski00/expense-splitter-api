package pl.sgorski.expense_splitter.features.expense.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Schema(
        name = "Detailed Expense Response",
        description = "Detailed expense data with participants and audit fields."
)
public record DetailedExpenseResponse(
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
                description = "Detailed description of the expense (optional).",
                example = "Team dinner after conference"
        )
        @Nullable String description,
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
                description = "List of participants and their settlements."
        )
        List<ExpenseParticipantResponse> participants,
        @Schema(
                description = "Date and time when expense was incurred.",
                example = "2026-03-18T20:00:00Z"
        )
        Instant expenseDate,
        @Schema(
                description = "Expense creation timestamp.",
                example = "2026-03-18T10:15:30Z"
        )
        Instant createdAt,
        @Schema(
                description = "Last update timestamp.",
                example = "2026-03-18T11:00:00Z"
        )
        Instant updatedAt,
        @Schema(
                description = "Soft-delete timestamp when expense was removed (optional).",
                example = "2026-03-19T09:00:00Z"
        )
        @Nullable Instant deletedAt
) { }
