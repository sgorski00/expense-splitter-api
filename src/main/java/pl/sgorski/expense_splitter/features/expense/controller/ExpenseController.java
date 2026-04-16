package pl.sgorski.expense_splitter.features.expense.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.expense_splitter.features.expense.dto.filter.ExpenseRole;
import pl.sgorski.expense_splitter.features.expense.dto.request.CreateExpenseRequest;
import pl.sgorski.expense_splitter.features.expense.dto.request.UpdateExpenseRequest;
import pl.sgorski.expense_splitter.features.expense.dto.response.DetailedExpenseResponse;
import pl.sgorski.expense_splitter.features.expense.dto.response.ExpenseResponse;
import pl.sgorski.expense_splitter.features.expense.mapper.ExpenseMapper;
import pl.sgorski.expense_splitter.features.expense.service.ExpenseService;
import pl.sgorski.expense_splitter.features.payment.dto.response.PaymentResponse;
import pl.sgorski.expense_splitter.features.payment.mapper.PaymentMapper;
import pl.sgorski.expense_splitter.features.payment.service.PaymentService;
import pl.sgorski.expense_splitter.security.authenticated.AuthenticatedUserResolver;

@RestController
@RequestMapping(value = "/expenses", version = "1.0.0")
@Tag(name = "Expenses", description = "Endpoints for expense management and settlement tracking.")
@RequiredArgsConstructor
public final class ExpenseController {

  private final AuthenticatedUserResolver authenticatedUserResolver;
  private final ExpenseService expenseService;
  private final ExpenseMapper expenseMapper;
  private final PaymentService paymentService;
  private final PaymentMapper paymentMapper;

  @GetMapping
  @Operation(
      summary = "List expenses",
      description = "Retrieves a paginated list of expenses involving the authenticated user.")
  @ApiResponse(responseCode = "200", description = "Expenses retrieved successfully.")
  public ResponseEntity<Page<ExpenseResponse>> getExpenses(
      @RequestParam(required = false) @Nullable ExpenseRole role,
      Authentication authentication,
      Pageable pageable) {
    var user = authenticatedUserResolver.requireUser(authentication);
    var result = expenseService.getExpenses(user, role, pageable);
    var response =
        result.map(
            expense -> expenseMapper.toResponse(expense, ExpenseRole.fromExpense(user, expense)));
    return ResponseEntity.ok(response);
  }

  @PostMapping
  @Operation(
      summary = "Create new expense",
      description = "Creates a new expense and assigns participants with their respective shares.")
  @ApiResponse(responseCode = "200", description = "Expense created successfully.")
  public ResponseEntity<DetailedExpenseResponse> createExpense(
      @RequestBody @Valid CreateExpenseRequest request, Authentication authentication) {
    var user = authenticatedUserResolver.requireUser(authentication);
    var command = expenseMapper.toCreateExpenseCommand(request);
    var result = expenseService.createExpense(user, command);
    var response = expenseMapper.toDetailedResponse(result, ExpenseRole.fromExpense(user, result));
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Get expense details",
      description =
          "Retrieves detailed information about a specific expense including all participants and their shares.")
  @ApiResponse(responseCode = "200", description = "Expense retrieved successfully.")
  public ResponseEntity<DetailedExpenseResponse> getExpense(
      @PathVariable UUID id, Authentication authentication) {
    var user = authenticatedUserResolver.requireUser(authentication);
    var result = expenseService.getExpense(id, user.getId());
    var response = expenseMapper.toDetailedResponse(result, ExpenseRole.fromExpense(user, result));
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{id}")
  @Operation(
      summary = "Update expense",
      description =
          "Updates expense details (title, description, amount). Only the creator can update an expense.")
  @ApiResponse(responseCode = "200", description = "Expense updated successfully.")
  public ResponseEntity<DetailedExpenseResponse> updateExpense(
      @PathVariable UUID id,
      @RequestBody @Valid UpdateExpenseRequest request,
      Authentication authentication) {
    var user = authenticatedUserResolver.requireUser(authentication);
    var command = expenseMapper.toUpdateExpenseCommand(request);
    var result = expenseService.updateExpense(id, user, command);
    var response = expenseMapper.toDetailedResponse(result, ExpenseRole.fromExpense(user, result));
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  @Operation(
      summary = "Delete expense",
      description = "Deletes an expense. Only the creator can delete an expense.")
  @ApiResponse(responseCode = "204", description = "Expense deleted successfully.")
  public ResponseEntity<Void> deleteExpense(@PathVariable UUID id, Authentication authentication) {
    var user = authenticatedUserResolver.requireUser(authentication);
    expenseService.deleteExpense(id, user);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}/payments")
  @Operation(
      summary = "List of expense payments",
      description = "Retrieves a paginated list of the selected expense payments.")
  @ApiResponse(responseCode = "200", description = "Payments list retrieved successfully.")
  public ResponseEntity<Page<PaymentResponse>> getPaymentsForExpense(
      @PathVariable UUID id, Pageable pageable, Authentication authentication) {
    var user = authenticatedUserResolver.requireUser(authentication);
    var expense = expenseService.getExpense(id, user.getId());
    var result =
        paymentService
            .getPaymentsForExpense(expense, pageable)
            .map(
                payment ->
                    paymentMapper.toResponse(payment, ExpenseRole.fromExpense(user, expense)));
    return ResponseEntity.ok(result);
  }

  @DeleteMapping("/{id}/participants/{participantId}")
  @Operation(
      summary = "Delete participant from an expense.",
      description =
          "Deletes a single participant from the expense. Operation cannot be done if the participant has made any payments for the expense. IMPORTANT: after removing the participant, the total expense amount will be substracted by the amount of the removed participant's share. Only the creator can remove a participant.")
  @ApiResponse(responseCode = "204", description = "Participant deleted successfully.")
  public ResponseEntity<Void> removeParticipant(
      @PathVariable UUID id, @PathVariable UUID participantId, Authentication authentication) {
    var user = authenticatedUserResolver.requireUser(authentication);
    expenseService.removeParticipant(id, participantId, user);
    return ResponseEntity.noContent().build();
  }
}
