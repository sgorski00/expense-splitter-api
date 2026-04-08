package pl.sgorski.expense_splitter.features.expense.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Schema(
    name = "Expense Participant Request",
    description = "Participant share definition used in expense payloads.")
public record ExpenseParticipantRequest(
    @Schema(
            description = "Participant user identifier.",
            example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull
        UUID userId) {}
