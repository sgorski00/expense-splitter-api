package pl.sgorski.expense_splitter.features.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.expense_splitter.features.expense.dto.response.ExpenseResponse;
import pl.sgorski.expense_splitter.features.payment.dto.request.CreatePaymentRequest;
import pl.sgorski.expense_splitter.features.payment.dto.request.UpdatePaymentRequest;
import pl.sgorski.expense_splitter.features.payment.dto.response.PaymentResponse;
import pl.sgorski.expense_splitter.features.user.domain.Role;
import pl.sgorski.expense_splitter.features.user.dto.response.UserResponse;

@RestController
@RequestMapping(value = "/payments", version = "1.0.0")
@Tag(name = "Payments", description = "Endpoints for payment recording and expense settlement.")
public final class PaymentController {

  @PostMapping
  @Operation(
      summary = "Create payment",
      description = "Records a new payment made by the authenticated user towards an expense.")
  @ApiResponses(
      value = {@ApiResponse(responseCode = "200", description = "Payment created successfully.")})
  public ResponseEntity<PaymentResponse> createPayment(
      @RequestBody @Valid CreatePaymentRequest request, Authentication authentication) {
    var user = new UserResponse(UUID.randomUUID(), "user@example.com", Role.USER, Instant.now());
    var expense =
        new ExpenseResponse(
            UUID.randomUUID(),
            "Expense no 1",
            BigDecimal.valueOf(999.99),
            BigDecimal.valueOf(33.33),
            Instant.now());
    var result =
        new PaymentResponse(
            UUID.randomUUID(), user, expense, BigDecimal.valueOf(300), Instant.now());
    return ResponseEntity.ok(result);
  }

  @GetMapping
  @Operation(
      summary = "List my payments",
      description = "Retrieves a paginated list of payments made by the authenticated user.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Payments retrieved successfully.")
      })
  public ResponseEntity<Page<PaymentResponse>> getMyPayments(Authentication authentication) {
    var user = new UserResponse(UUID.randomUUID(), "user@example.com", Role.USER, Instant.now());
    var expense =
        new ExpenseResponse(
            UUID.randomUUID(),
            "Expense no 1",
            BigDecimal.valueOf(999.99),
            BigDecimal.valueOf(33.33),
            Instant.now());
    var Payment =
        new PaymentResponse(
            UUID.randomUUID(), user, expense, BigDecimal.valueOf(300), Instant.now());
    var result = new PageImpl<>(List.of(Payment));
    return ResponseEntity.ok(result);
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Get payment details",
      description = "Retrieves detailed information about a specific payment.")
  @ApiResponses(
      value = {@ApiResponse(responseCode = "200", description = "Payment retrieved successfully.")})
  public ResponseEntity<PaymentResponse> getPayment(
      @PathVariable UUID id, Authentication authentication) {
    var user = new UserResponse(UUID.randomUUID(), "user@example.com", Role.USER, Instant.now());
    var expense =
        new ExpenseResponse(
            UUID.randomUUID(),
            "Expense no 1",
            BigDecimal.valueOf(999.99),
            BigDecimal.valueOf(33.33),
            Instant.now());
    var Payment =
        new PaymentResponse(
            UUID.randomUUID(), user, expense, BigDecimal.valueOf(300), Instant.now());
    return ResponseEntity.ok(Payment);
  }

  @PatchMapping("/{id}")
  @Operation(summary = "Update payment", description = "Updates the amount of an existing payment.")
  @ApiResponses(
      value = {@ApiResponse(responseCode = "200", description = "Payment updated successfully.")})
  public ResponseEntity<PaymentResponse> updatePayment(
      @PathVariable UUID id,
      @RequestBody @Valid UpdatePaymentRequest request,
      Authentication authentication) {
    var user = new UserResponse(UUID.randomUUID(), "user@example.com", Role.USER, Instant.now());
    var expense =
        new ExpenseResponse(
            UUID.randomUUID(),
            "Expense no 1",
            BigDecimal.valueOf(999.99),
            BigDecimal.valueOf(33.33),
            Instant.now());
    var Payment =
        new PaymentResponse(
            UUID.randomUUID(), user, expense, BigDecimal.valueOf(300), Instant.now());
    return ResponseEntity.ok(Payment);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete payment", description = "Deletes a payment record.")
  @ApiResponses(
      value = {@ApiResponse(responseCode = "204", description = "Payment deleted successfully.")})
  public ResponseEntity<Void> deletePayment(@PathVariable UUID id, Authentication authentication) {
    return ResponseEntity.noContent().build();
  }
}
