package pl.sgorski.expense_splitter.features.expense.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import pl.sgorski.expense_splitter.features.expense.dto.filter.ExpenseRole;

@Schema(name = "Expense Response", description = "Basic expense data returned in listings.")
public record ExpenseResponse(
    @Schema(
            description = "Expense unique identifier.",
            example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
    @Schema(description = "Expense title.", example = "Dinner in Rome") String title,
    @Schema(description = "Role of the user in the expense", example = "PAYER") ExpenseRole role,
    @Schema(description = "Total expense amount.", example = "999.99") BigDecimal amountTotal,
    @Schema(
            description = "Date and time when expense was incurred.",
            example = "2026-03-18T20:00:00Z")
        Instant expenseDate) {}
