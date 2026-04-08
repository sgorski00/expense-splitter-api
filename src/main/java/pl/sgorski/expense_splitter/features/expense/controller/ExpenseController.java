package pl.sgorski.expense_splitter.features.expense.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import pl.sgorski.expense_splitter.security.authenticated.AuthenticatedUserResolver;

@RestController
@RequestMapping(value = "/expenses", version = "1.0.0")
@Tag(name = "Expenses", description = "Endpoints for expense management and settlement tracking.")
@RequiredArgsConstructor
public final class ExpenseController {

  private final AuthenticatedUserResolver authenticatedUserResolver;
  private final ExpenseService expenseService;
  private final ExpenseMapper expenseMapper;

  @GetMapping
  @Operation(
      summary = "List expenses",
      description = "Retrieves a paginated list of expenses involving the authenticated user.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Expenses retrieved successfully.")
      })
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
  @ApiResponses(
      value = {@ApiResponse(responseCode = "200", description = "Expense created successfully.")})
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
  @ApiResponses(
      value = {@ApiResponse(responseCode = "200", description = "Expense retrieved successfully.")})
  public ResponseEntity<DetailedExpenseResponse> getExpense(
      @PathVariable UUID id, Authentication authentication) {
    var user = authenticatedUserResolver.requireUser(authentication);
    var result = expenseService.getExpense(id, user);
    var response = expenseMapper.toDetailedResponse(result, ExpenseRole.fromExpense(user, result));
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{id}")
  @Operation(
      summary = "Update expense",
      description =
          "Updates expense details (title, description, amount). Only the creator can update an expense.")
  @ApiResponses(
      value = {@ApiResponse(responseCode = "200", description = "Expense updated successfully.")})
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
  @ApiResponses(
      value = {@ApiResponse(responseCode = "204", description = "Expense deleted successfully.")})
  public ResponseEntity<Void> deleteExpense(@PathVariable UUID id, Authentication authentication) {
    var user = authenticatedUserResolver.requireUser(authentication);
    expenseService.deleteExpense(id, user);
    return ResponseEntity.noContent().build();
  }
}
