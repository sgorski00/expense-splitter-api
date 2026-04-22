package pl.sgorski.expense_splitter.features.statistics.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.sgorski.expense_splitter.features.statistics.dto.filter.DateRange;
import pl.sgorski.expense_splitter.features.statistics.dto.response.ExpenseStatisticsResponse;
import pl.sgorski.expense_splitter.features.statistics.dto.response.PaymentStatisticsResponse;
import pl.sgorski.expense_splitter.features.statistics.service.StatisticsService;
import pl.sgorski.expense_splitter.security.authenticated.AuthenticatedUserResolver;

@RestController
@RequestMapping(value = "/statistics", version = "1.0.0")
@Tag(
    name = "Statistics",
    description = "Endpoints for viewing periodical statistics of expenses and payments.")
@RequiredArgsConstructor
public final class StatisticsController {

  private final AuthenticatedUserResolver authenticatedUserResolver;
  private final StatisticsService statisticsService;

  @GetMapping("/expenses")
  @Operation(
      summary = "Get expense statistics",
      description =
          "Retrieves statistics for expenses involving the authenticated user within a specified date range.")
  @ApiResponse(responseCode = "200", description = "Expense statistics retrieved successfully.")
  public ResponseEntity<ExpenseStatisticsResponse> getExpenseStatistics(
      @Parameter(description = "Docker-compose: expense-splitter-api", example = "2026-01-01")
          @RequestParam
          LocalDate from,
      @Parameter(
              description = "End date (inclusive) in ISO-8601 format (yyyy-MM-dd)",
              example = "2026-03-31")
          @RequestParam
          LocalDate to,
      Authentication authentication) {
    var userId = authenticatedUserResolver.requireUserId(authentication);
    var result = statisticsService.getExpenseStatistics(userId, new DateRange(from, to));
    return ResponseEntity.ok(result);
  }

  @GetMapping("/payments")
  @Operation(
      summary = "Get payment statistics",
      description =
          "Retrieves statistics for payments made by the authenticated user within a specified date range.")
  @ApiResponse(responseCode = "200", description = "Payment statistics retrieved successfully.")
  public ResponseEntity<PaymentStatisticsResponse> getPaymentStatistics(
      @Parameter(
              description = "Start date (inclusive) in ISO-8601 format (yyyy-MM-dd)",
              example = "2026-01-01")
          @RequestParam
          LocalDate from,
      @Parameter(
              description = "End date (inclusive) in ISO-8601 format (yyyy-MM-dd)",
              example = "2026-03-31")
          @RequestParam
          LocalDate to,
      Authentication authentication) {
    var userId = authenticatedUserResolver.requireUserId(authentication);
    var result = statisticsService.getPaymentStatistics(userId, new DateRange(from, to));
    return ResponseEntity.ok(result);
  }
}
