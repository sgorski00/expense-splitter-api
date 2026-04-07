package pl.sgorski.expense_splitter.features.expense.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import pl.sgorski.expense_splitter.features.expense.domain.SplitType;
import pl.sgorski.expense_splitter.features.user.dto.response.UserResponse;

@Schema(
    name = "Detailed Expense Response",
    description = "Detailed expense data with participants and audit fields.")
public record DetailedExpenseResponse(
    @Schema(
            description = "Expense unique identifier.",
            example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
    @Schema(description = "Expense title.", example = "Dinner in Rome") String title,
    @Schema(
            description = "Detailed description of the expense (optional).",
            example = "Team dinner after conference")
        @Nullable String description,
    @Schema(description = "User who paid for the expense.", implementation = UserResponse.class)
        UserResponse payer,
    @Schema(description = "Total expense amount.", example = "999.99") BigDecimal amountTotal,
    @Schema(description = "Method of splitting the expense among participants.", example = "EQUAL")
        SplitType splitType,
    @Schema(description = "List of shares between the participants.")
        List<ExpenseShareResponse> shares,
    @Schema(
            description = "Date and time when expense was incurred.",
            example = "2026-03-18T20:00:00Z")
        Instant expenseDate,
    @Schema(description = "Expense creation timestamp.", example = "2026-03-18T10:15:30Z")
        Instant createdAt,
    @Schema(description = "Last update timestamp.", example = "2026-03-18T11:00:00Z")
        Instant updatedAt) {}
