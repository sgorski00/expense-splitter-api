package pl.sgorski.expense_splitter.features.statistics.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(
    name = "Payment Statistics Response",
    description = "Summary statistics for payments in a given period.")
public record PaymentStatisticsResponse(
    @Schema(description = "Total number of payments in the period.", example = "8")
        long totalPayments,
    @Schema(description = "Total amount of all payments in the period.", example = "1200.00")
        BigDecimal totalAmount,
    @Schema(description = "Average amount per payment in the period.", example = "150.00")
        BigDecimal averagePerPayment) {}
