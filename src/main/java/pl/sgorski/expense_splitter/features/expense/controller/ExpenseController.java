package pl.sgorski.expense_splitter.features.expense.controller;

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
import pl.sgorski.expense_splitter.features.user.dto.response.UserResponse;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping(value = "/expenses", version = "1")
public final class ExpenseController {

    @GetMapping
    public ResponseEntity<Page<ExpenseResponse>> getMyExpenses(
            Authentication authentication
    ) {
        var result = new PageImpl<>(List.of(new ExpenseResponse(1L, "Expense no 1", BigDecimal.valueOf(999.99), BigDecimal.valueOf(333.33), Instant.now()))); // TODO: implement
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<DetailedExpenseResponse> createExpense(
            @RequestBody @Valid CreateExpenseRequest request,
            Authentication authentication
    ) {
        var user = new UserResponse(1L, "user@example.com", "USER", Instant.now());
        var participants = List.of(new ExpenseParticipantResponse(user, BigDecimal.valueOf(333.33)));
        var result = new DetailedExpenseResponse(1L, "Expense no 1", "Description of this expense", BigDecimal.valueOf(999.99), BigDecimal.valueOf(333.33), participants, Instant.now(), Instant.now(), Instant.now(), null); // TODO: implement
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetailedExpenseResponse> getExpense(
            @PathVariable Long id,
            Authentication authentication
    ) {
        var user = new UserResponse(1L, "user@example.com", "USER", Instant.now());
        var participants = List.of(new ExpenseParticipantResponse(user, BigDecimal.valueOf(333.33)));
        var result = new DetailedExpenseResponse(1L, "Expense no 1", "Description of this expense", BigDecimal.valueOf(999.99), BigDecimal.valueOf(333.33), participants, Instant.now(), Instant.now(), Instant.now(), null); // TODO: implement
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DetailedExpenseResponse> updateExpense(
            @PathVariable Long id,
            @RequestBody @Valid UpdateExpenseRequest request,
            Authentication authentication
    ) {
        // only creator of the expense can update it
        var user = new UserResponse(1L, "user@example.com", "USER", Instant.now());
        var participants = List.of(new ExpenseParticipantResponse(user, BigDecimal.valueOf(333.33)));
        var result = new DetailedExpenseResponse(1L, "Expense no 1", "Description of this expense", BigDecimal.valueOf(999.99), BigDecimal.valueOf(333.33), participants, Instant.now(), Instant.now(), Instant.now(), null); // TODO: implement
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DetailedExpenseResponse> deleteExpense(
            @PathVariable Long id,
            Authentication authentication
    ) {
        // only creator of the expense can delete it
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<Page<ExpenseParticipantResponse>> getExpenseParticipants(
            @PathVariable Long id,
            Pageable pageable,
            Authentication authentication
    ) {
        var user = new UserResponse(1L, "user@example.com", "USER", Instant.now());
        var result = new PageImpl<>(List.of(new ExpenseParticipantResponse(user, BigDecimal.valueOf(333.33))));
        return ResponseEntity.ok(result);
    }
}
