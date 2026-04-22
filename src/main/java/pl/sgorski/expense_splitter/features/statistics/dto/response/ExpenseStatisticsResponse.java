package pl.sgorski.expense_splitter.features.statistics.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(
    name = "Expense Statistics Response",
    description = "Summary statistics for expenses in a given period.")
public record ExpenseStatisticsResponse(
    @Schema(description = "Total number of expenses in the period.", example = "5")
        long totalExpenses,
    @Schema(description = "Total amount of all expenses in the period.", example = "1500.00")
        BigDecimal totalAmount,
    @Schema(description = "Average amount per expense in the period.", example = "300.00")
        BigDecimal averagePerExpense) {}
