package pl.sgorski.expense_splitter.features.expense.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import pl.sgorski.expense_splitter.features.user.dto.response.UserResponse;

@Schema(
    name = "Expense Participant Response",
    description = "Participant settlement view for an expense.")
public record ExpenseParticipantResponse(
    @Schema(description = "Participant basic user data.") UserResponse user,
    @Schema(description = "Amount owed by this participant.", example = "333.33")
        BigDecimal amountOwed) {}
