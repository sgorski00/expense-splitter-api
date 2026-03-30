package pl.sgorski.expense_splitter.features.expense.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.expense_splitter.features.expense.dto.request.CreateExpenseRequest;
import pl.sgorski.expense_splitter.features.expense.dto.request.UpdateExpenseRequest;
import pl.sgorski.expense_splitter.features.expense.dto.response.DetailedExpenseResponse;
import pl.sgorski.expense_splitter.features.expense.dto.response.ExpenseParticipantResponse;
import pl.sgorski.expense_splitter.features.expense.dto.response.ExpenseResponse;
import pl.sgorski.expense_splitter.features.user.domain.Role;
import pl.sgorski.expense_splitter.features.user.dto.response.UserResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/expenses", version = "1.0.0")
@Tag(name = "Expenses", description = "Endpoints for expense management and settlement tracking.")
public final class ExpenseController {

    @GetMapping
    @Operation(
            summary = "List my expenses",
            description = "Retrieves a paginated list of expenses involving the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Expenses retrieved successfully."
            )
    })
    public ResponseEntity<Page<ExpenseResponse>> getMyExpenses(
            Authentication authentication
    ) {
        var result = new PageImpl<>(List.of(new ExpenseResponse(UUID.randomUUID(), "Expense no 1", BigDecimal.valueOf(999.99), BigDecimal.valueOf(333.33), Instant.now()))); // TODO: implement
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @Operation(
            summary = "Create new expense",
            description = "Creates a new expense and assigns participants with their respective shares."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Expense created successfully."
            )
    })
    public ResponseEntity<DetailedExpenseResponse> createExpense(
            @RequestBody @Valid CreateExpenseRequest request,
            Authentication authentication
    ) {
        var user = new UserResponse(UUID.randomUUID(), "user@example.com", Role.USER, Instant.now());
        var participants = List.of(new ExpenseParticipantResponse(user, BigDecimal.valueOf(333.33)));
        var result = new DetailedExpenseResponse(UUID.randomUUID(), "Expense no 1", "Description of this expense", BigDecimal.valueOf(999.99), BigDecimal.valueOf(333.33), participants, Instant.now(), Instant.now(), Instant.now(), null); // TODO: implement
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get expense details",
            description = "Retrieves detailed information about a specific expense including all participants and their shares."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Expense retrieved successfully."
            )
    })
    public ResponseEntity<DetailedExpenseResponse> getExpense(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        var user = new UserResponse(UUID.randomUUID(), "user@example.com", Role.USER, Instant.now());
        var participants = List.of(new ExpenseParticipantResponse(user, BigDecimal.valueOf(333.33)));
        var result = new DetailedExpenseResponse(UUID.randomUUID(), "Expense no 1", "Description of this expense", BigDecimal.valueOf(999.99), BigDecimal.valueOf(333.33), participants, Instant.now(), Instant.now(), Instant.now(), null); // TODO: implement
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Update expense",
            description = "Updates expense details (title, description, amount). Only the creator can update an expense."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Expense updated successfully."
            )
    })
    public ResponseEntity<DetailedExpenseResponse> updateExpense(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateExpenseRequest request,
            Authentication authentication
    ) {
        // only creator of the expense can update it
        var user = new UserResponse(UUID.randomUUID(), "user@example.com", Role.USER, Instant.now());
        var participants = List.of(new ExpenseParticipantResponse(user, BigDecimal.valueOf(333.33)));
        var result = new DetailedExpenseResponse(UUID.randomUUID(), "Expense no 1", "Description of this expense", BigDecimal.valueOf(999.99), BigDecimal.valueOf(333.33), participants, Instant.now(), Instant.now(), Instant.now(), null); // TODO: implement
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete expense",
            description = "Deletes an expense. Only the creator can delete an expense."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Expense deleted successfully."
            )
    })
    public ResponseEntity<Void> deleteExpense(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        // only creator of the expense can delete it
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/participants")
    @Operation(
            summary = "Get expense participants",
            description = "Retrieves a paginated list of participants for a specific expense and their shares."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Participants retrieved successfully."
            )
    })
    public ResponseEntity<Page<ExpenseParticipantResponse>> getExpenseParticipants(
            @PathVariable UUID id,
            Pageable pageable,
            Authentication authentication
    ) {
        var user = new UserResponse(UUID.randomUUID(), "user@example.com", Role.USER, Instant.now());
        var result = new PageImpl<>(List.of(new ExpenseParticipantResponse(user, BigDecimal.valueOf(333.33))));
        return ResponseEntity.ok(result);
    }
}
