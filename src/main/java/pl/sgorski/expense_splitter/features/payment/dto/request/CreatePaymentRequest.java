package pl.sgorski.expense_splitter.features.payment.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreatePaymentRequest(
        @NotNull Long expenseId,
        @NotNull @Positive BigDecimal amount
) { }
