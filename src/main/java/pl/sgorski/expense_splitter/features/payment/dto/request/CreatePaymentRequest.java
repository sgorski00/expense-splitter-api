package pl.sgorski.expense_splitter.features.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(
    name = "Create Payment Request",
    description = "Payload used to register a payment for an expense.")
public record CreatePaymentRequest(
    @Schema(
            description = "Expense identifier to which the payment is assigned.",
            example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull
        UUID expenseId,
    @Schema(description = "Payment amount.", example = "300.00") @NotNull @Positive
        BigDecimal amount) {}
