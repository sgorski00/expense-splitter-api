package pl.sgorski.expense_splitter.features.payment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import pl.sgorski.expense_splitter.features.expense.dto.response.ExpenseResponse;
import pl.sgorski.expense_splitter.features.user.dto.response.UserResponse;

@Schema(name = "Payment Response", description = "Payment details returned by API.")
public record PaymentResponse(
    @Schema(
            description = "Payment unique identifier.",
            example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
    @Schema(description = "User who made the payment.") UserResponse payer,
    @Schema(description = "Expense for which the payment was made.") ExpenseResponse expense,
    @Schema(description = "Payment amount.", example = "300.00") BigDecimal amount,
    @Schema(description = "Payment creation timestamp.", example = "2026-03-18T12:30:00Z")
        Instant createdAt,
    @Schema(description = "Payment update timestamp.", example = "2026-03-19T12:35:00Z")
        Instant updatedAt) {}
