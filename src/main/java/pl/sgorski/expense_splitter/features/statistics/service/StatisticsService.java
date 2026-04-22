package pl.sgorski.expense_splitter.features.statistics.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.features.expense.repository.ExpenseRepository;
import pl.sgorski.expense_splitter.features.payment.repository.PaymentRepository;
import pl.sgorski.expense_splitter.features.statistics.dto.filter.DateRange;
import pl.sgorski.expense_splitter.features.statistics.dto.response.ExpenseStatisticsResponse;
import pl.sgorski.expense_splitter.features.statistics.dto.response.PaymentStatisticsResponse;

@Service
@RequiredArgsConstructor
public final class StatisticsService {

  private final ExpenseRepository expenseRepository;
  private final PaymentRepository paymentRepository;

  public ExpenseStatisticsResponse getExpenseStatistics(UUID userId, DateRange dateRange) {
    var totalExpenses =
        expenseRepository.countByUserAndDateRange(
            userId, dateRange.fromInclusive(), dateRange.toExclusive());
    var totalAmount =
        totalExpenses > 0
            ? expenseRepository.sumAmountByUserAndDateRange(
                userId, dateRange.fromInclusive(), dateRange.toExclusive())
            : BigDecimal.ZERO;
    var averagePerExpense =
        totalExpenses > 0
            ? totalAmount.divide(BigDecimal.valueOf(totalExpenses), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

    return new ExpenseStatisticsResponse(totalExpenses, totalAmount, averagePerExpense);
  }

  public PaymentStatisticsResponse getPaymentStatistics(UUID userId, DateRange dateRange) {
    var totalPayments =
        paymentRepository.countByPayerAndDateRange(
            userId, dateRange.fromInclusive(), dateRange.toExclusive());
    var totalAmount =
        totalPayments > 0
            ? paymentRepository.sumAmountByPayerAndDateRange(
                userId, dateRange.fromInclusive(), dateRange.toExclusive())
            : BigDecimal.ZERO;
    var averagePerPayment =
        totalPayments > 0
            ? totalAmount.divide(BigDecimal.valueOf(totalPayments), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

    return new PaymentStatisticsResponse(totalPayments, totalAmount, averagePerPayment);
  }
}
