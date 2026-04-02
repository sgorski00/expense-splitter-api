package pl.sgorski.expense_splitter.features.expense.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Schema(
    name = "Expense Participant Request",
    description = "Participant share definition used in expense payloads.")
public record ExpenseParticipantRequest(
    @Schema(description = "Participant user identifier.", example = "2") @NotNull Long userId,
    @Schema(description = "Amount assigned to participant.", example = "333.33") @NotNull @Positive
        BigDecimal value) {}
