package pl.sgorski.expense_splitter.features.expense.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import org.jspecify.annotations.Nullable;

@Schema(name = "Create Expense Request", description = "Payload used to create a new expense.")
public record CreateExpenseRequest(
    @Schema(description = "Expense title.", example = "Dinner in Rome") @NotBlank String title,
    @Schema(
            description = "Detailed description of the expense (optional).",
            example = "Team dinner after conference")
        @Nullable String description,
    @Schema(description = "Total expense amount.", example = "999.99") @NotNull @Positive
        BigDecimal amount,
    @Schema(description = "Set of participants and their shares.")
        @Nullable Set<ExpenseParticipantRequest> participants,
    @Schema(
            description = "Date and time when expense was incurred.",
            example = "2026-03-18T20:00:00Z")
        @NotNull
        Instant expenseDate) {}
