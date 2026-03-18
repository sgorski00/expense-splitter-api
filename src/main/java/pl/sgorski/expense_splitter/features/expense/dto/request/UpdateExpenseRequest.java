package pl.sgorski.expense_splitter.features.expense.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;

@Schema(
        name = "Update Expense Request",
        description = "Payload used to update existing expense details. All fields are optional. If a field is not provided, the corresponding expense attribute will remain unchanged."
)
public record UpdateExpenseRequest(
        @Schema(
                description = "New expense title.",
                example = "Lunch in Venice"
        )
        @Nullable String title,
        @Schema(
                description = "Updated expense description.",
                example = "Team lunch meeting"
        )
        @Nullable String description,
        @Schema(
                description = "Updated total expense amount.",
                example = "750.50"
        )
        @Nullable BigDecimal amount
) { }
