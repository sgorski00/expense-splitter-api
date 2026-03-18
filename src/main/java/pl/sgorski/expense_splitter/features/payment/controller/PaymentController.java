package pl.sgorski.expense_splitter.features.payment.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.expense_splitter.features.expense.dto.response.ExpenseResponse;
import pl.sgorski.expense_splitter.features.payment.dto.request.CreatePaymentRequest;
import pl.sgorski.expense_splitter.features.payment.dto.request.UpdatePaymentRequest;
import pl.sgorski.expense_splitter.features.payment.dto.response.PaymentResponse;
import pl.sgorski.expense_splitter.features.user.dto.response.UserResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping(value = "/payments", version = "1.0.0")
public final class PaymentController {

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestBody @Valid CreatePaymentRequest request,
            Authentication authentication
    ) {
        var user = new UserResponse(1L, "user@example.com", "USER", Instant.now());
        var expense = new ExpenseResponse(1L, "Expense no 1", BigDecimal.valueOf(999.99), BigDecimal.valueOf(33.33), Instant.now());
        var result = new PaymentResponse(1L, user, expense, BigDecimal.valueOf(300), Instant.now());
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<Page<PaymentResponse>> getMyPayments(
            Authentication authentication
    ) {
        var user = new UserResponse(1L, "user@example.com", "USER", Instant.now());
        var expense = new ExpenseResponse(1L, "Expense no 1", BigDecimal.valueOf(999.99), BigDecimal.valueOf(33.33), Instant.now());
        var Payment = new PaymentResponse(1L, user, expense, BigDecimal.valueOf(300), Instant.now());
        var result = new PageImpl<>(List.of(Payment));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(
            @PathVariable Long id,
            Authentication authentication
    ) {
        var user = new UserResponse(1L, "user@example.com", "USER", Instant.now());
        var expense = new ExpenseResponse(1L, "Expense no 1", BigDecimal.valueOf(999.99), BigDecimal.valueOf(33.33), Instant.now());
        var Payment = new PaymentResponse(1L, user, expense, BigDecimal.valueOf(300), Instant.now());
        return ResponseEntity.ok(Payment);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PaymentResponse> updatePayment(
            @PathVariable Long id,
            @RequestBody @Valid UpdatePaymentRequest request,
            Authentication authentication
    ) {
        var user = new UserResponse(1L, "user@example.com", "USER", Instant.now());
        var expense = new ExpenseResponse(1L, "Expense no 1", BigDecimal.valueOf(999.99), BigDecimal.valueOf(33.33), Instant.now());
        var Payment = new PaymentResponse(1L, user, expense, BigDecimal.valueOf(300), Instant.now());
        return ResponseEntity.ok(Payment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PaymentResponse> deletePayment(
            @PathVariable Long id,
            Authentication authentication
    ) {
        //maybe without soft delete?
        return ResponseEntity.noContent().build();
    }
}
