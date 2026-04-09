package pl.sgorski.expense_splitter.features.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.expense_splitter.features.expense.dto.filter.ExpenseRole;
import pl.sgorski.expense_splitter.features.payment.dto.request.CreatePaymentRequest;
import pl.sgorski.expense_splitter.features.payment.dto.response.PaymentResponse;
import pl.sgorski.expense_splitter.features.payment.mapper.PaymentMapper;
import pl.sgorski.expense_splitter.features.payment.service.PaymentService;
import pl.sgorski.expense_splitter.security.authenticated.AuthenticatedUserResolver;

@RestController
@RequestMapping(value = "/payments", version = "1.0.0")
@Tag(name = "Payments", description = "Endpoints for payment recording and expense settlement.")
@RequiredArgsConstructor
public final class PaymentController {

  private final AuthenticatedUserResolver authenticatedUserResolver;
  private final PaymentService paymentService;
  private final PaymentMapper paymentMapper;

  @PostMapping
  @Operation(
      summary = "Create payment",
      description = "Records a new payment made by the authenticated user towards an expense.")
  @ApiResponses(
      value = {@ApiResponse(responseCode = "200", description = "Payment created successfully.")})
  public ResponseEntity<PaymentResponse> createPayment(
      @RequestBody @Valid CreatePaymentRequest request, Authentication authentication) {
    var user = authenticatedUserResolver.requireUser(authentication);
    var command = paymentMapper.toCreateCommand(request);
    var result = paymentService.createPayment(command, user);
    var response =
        paymentMapper.toResponse(result, ExpenseRole.fromExpense(user, result.getExpense()));
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Get payment details",
      description = "Retrieves detailed information about a specific payment.")
  @ApiResponses(
      value = {@ApiResponse(responseCode = "200", description = "Payment retrieved successfully.")})
  public ResponseEntity<PaymentResponse> getPayment(
      @PathVariable UUID id, Authentication authentication) {
    var user = authenticatedUserResolver.requireUser(authentication);
    var result = paymentService.getPayment(id, user);
    var response =
        paymentMapper.toResponse(result, ExpenseRole.fromExpense(user, result.getExpense()));
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete payment", description = "Deletes a payment record.")
  @ApiResponses(
      value = {@ApiResponse(responseCode = "204", description = "Payment deleted successfully.")})
  public ResponseEntity<Void> deletePayment(@PathVariable UUID id, Authentication authentication) {
    var user = authenticatedUserResolver.requireUser(authentication);
    paymentService.deletePayment(id, user);
    return ResponseEntity.noContent().build();
  }
}
