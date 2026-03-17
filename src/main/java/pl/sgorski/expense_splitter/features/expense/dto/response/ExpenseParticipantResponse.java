package pl.sgorski.expense_splitter.features.expense.dto.response;

import pl.sgorski.expense_splitter.features.user.dto.response.UserResponse;

import java.math.BigDecimal;

public record ExpenseParticipantResponse(
        UserResponse user,
        BigDecimal amountOwed
) { }
