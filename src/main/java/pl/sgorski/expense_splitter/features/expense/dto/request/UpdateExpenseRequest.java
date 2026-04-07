package pl.sgorski.expense_splitter.features.expense.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import org.jspecify.annotations.Nullable;
import pl.sgorski.expense_splitter.validator.text.NullOrNotBlank;

@Schema(
    name = "Update Expense Request",
    description =
        "Payload used to update existing expense details. All fields are optional. If a field is not provided, the corresponding expense attribute will remain unchanged.")
public record UpdateExpenseRequest(
    @Schema(description = "New expense title.", example = "Lunch in Venice")
        @Nullable
        @NullOrNotBlank
        String title,
    @Schema(description = "Updated expense description.", example = "Team lunch meeting")
        @Nullable String description,
    @Schema(
            description = "Date and time when expense was incurred.",
            example = "2026-03-18T20:00:00Z")
        @Nullable Instant expenseDate) {}
