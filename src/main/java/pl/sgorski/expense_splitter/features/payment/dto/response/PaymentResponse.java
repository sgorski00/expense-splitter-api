package pl.sgorski.expense_splitter.features.payment.dto.response;

import pl.sgorski.expense_splitter.features.expense.dto.response.ExpenseResponse;
import pl.sgorski.expense_splitter.features.user.dto.response.UserResponse;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        Long id,
        UserResponse user,
        ExpenseResponse expense,
        BigDecimal amount,
        Instant createdAt
) { }
