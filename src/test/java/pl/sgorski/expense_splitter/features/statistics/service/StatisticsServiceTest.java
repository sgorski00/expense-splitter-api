package pl.sgorski.expense_splitter.features.statistics.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.expense_splitter.features.expense.repository.ExpenseRepository;
import pl.sgorski.expense_splitter.features.payment.repository.PaymentRepository;
import pl.sgorski.expense_splitter.features.statistics.dto.filter.DateRange;

@ExtendWith(MockitoExtension.class)
public class StatisticsServiceTest {

  @Mock private ExpenseRepository expenseRepository;
  @Mock private PaymentRepository paymentRepository;
  @InjectMocks private StatisticsService statisticsService;

  @Test
  void getExpenseStatistics_shouldReturnCorrectResponse_whenTotalExpensesIsZero() {
    var userId = UUID.randomUUID();
    var from = LocalDate.of(2026, 1, 1);
    var to = LocalDate.of(2026, 1, 31);
    var dateRange = new DateRange(from, to);
    when(expenseRepository.countByUserAndDateRange(
            userId, dateRange.fromInclusive(), dateRange.toExclusive()))
        .thenReturn(0L);

    var result = statisticsService.getExpenseStatistics(userId, dateRange);

    assertEquals(0L, result.totalExpenses());
    assertEquals(0, result.totalAmount().compareTo(BigDecimal.ZERO));
    assertEquals(0, result.averagePerExpense().compareTo(BigDecimal.ZERO));
  }

  @Test
  void getExpenseStatistics_shouldReturnCorrectResponse_whenTotalExpensesIsGreaterThanZero() {
    var userId = UUID.randomUUID();
    var from = LocalDate.of(2026, 1, 1);
    var to = LocalDate.of(2026, 1, 31);
    var dateRange = new DateRange(from, to);
    when(expenseRepository.countByUserAndDateRange(
            userId, dateRange.fromInclusive(), dateRange.toExclusive()))
        .thenReturn(2L);
    when(expenseRepository.sumAmountByUserAndDateRange(
            userId, dateRange.fromInclusive(), dateRange.toExclusive()))
        .thenReturn(BigDecimal.valueOf(200));

    var result = statisticsService.getExpenseStatistics(userId, dateRange);

    assertEquals(2L, result.totalExpenses());
    assertEquals(0, result.totalAmount().compareTo(BigDecimal.valueOf(200)));
    assertEquals(0, result.averagePerExpense().compareTo(BigDecimal.valueOf(100)));
  }

  @Test
  void getPaymentStatistics_shouldReturnCorrectResponse_whenTotalPaymentsIsZero() {
    var userId = UUID.randomUUID();
    var from = LocalDate.of(2026, 1, 1);
    var to = LocalDate.of(2026, 1, 31);
    var dateRange = new DateRange(from, to);
    when(paymentRepository.countByPayerAndDateRange(
            userId, dateRange.fromInclusive(), dateRange.toExclusive()))
        .thenReturn(0L);

    var result = statisticsService.getPaymentStatistics(userId, dateRange);

    assertEquals(0L, result.totalPayments());
    assertEquals(0, result.totalAmount().compareTo(BigDecimal.ZERO));
    assertEquals(0, result.averagePerPayment().compareTo(BigDecimal.ZERO));
  }

  @Test
  void getPaymentStatistics_shouldReturnCorrectResponse_whenTotalPaymentsIsGreaterThanZero() {
    var userId = UUID.randomUUID();
    var from = LocalDate.of(2026, 1, 1);
    var to = LocalDate.of(2026, 1, 31);
    var dateRange = new DateRange(from, to);
    when(paymentRepository.countByPayerAndDateRange(
            userId, dateRange.fromInclusive(), dateRange.toExclusive()))
        .thenReturn(2L);
    when(paymentRepository.sumAmountByPayerAndDateRange(
            userId, dateRange.fromInclusive(), dateRange.toExclusive()))
        .thenReturn(BigDecimal.valueOf(200));

    var result = statisticsService.getPaymentStatistics(userId, dateRange);

    assertEquals(2L, result.totalPayments());
    assertEquals(0, result.totalAmount().compareTo(BigDecimal.valueOf(200)));
    assertEquals(0, result.averagePerPayment().compareTo(BigDecimal.valueOf(100)));
  }
}
