package pl.sgorski.expense_splitter.features.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(
        name = "Create Payment Request",
        description = "Payload used to register a payment for an expense."
)
public record CreatePaymentRequest(
        @Schema(
                description = "Expense identifier to which the payment is assigned.",
                example = "1"
        )
        @NotNull Long expenseId,
        @Schema(
                description = "Payment amount.",
                example = "300.00"
        )
        @NotNull @Positive BigDecimal amount
) { }
