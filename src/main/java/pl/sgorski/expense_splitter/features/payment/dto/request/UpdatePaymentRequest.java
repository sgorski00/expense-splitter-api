package pl.sgorski.expense_splitter.features.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(
        name = "Update Payment Request",
        description = "Payload used to update an existing payment."
)
public record UpdatePaymentRequest(
        @Schema(
                description = "Updated payment amount.",
                example = "350.00"
        )
        @NotNull @Positive BigDecimal amount
) { }
