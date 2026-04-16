package pl.sgorski.expense_splitter.features.payment.service;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.exceptions.expense.ExpenseNotFoundException;
import pl.sgorski.expense_splitter.exceptions.payment.PaymentNotFoundException;
import pl.sgorski.expense_splitter.exceptions.payment.PaymentValidationException;
import pl.sgorski.expense_splitter.features.expense.domain.Expense;
import pl.sgorski.expense_splitter.features.expense.repository.ExpenseRepository;
import pl.sgorski.expense_splitter.features.payment.domain.Payment;
import pl.sgorski.expense_splitter.features.payment.dto.command.CreatePaymentCommand;
import pl.sgorski.expense_splitter.features.payment.repository.PaymentRepository;
import pl.sgorski.expense_splitter.features.user.domain.User;

@Service
@RequiredArgsConstructor
public class PaymentService {

  private final ExpenseRepository expenseRepository;
  private final PaymentRepository paymentRepository;

  public Payment getPayment(UUID id, UUID currentUserId) {
    var payment =
        paymentRepository.findById(id).orElseThrow(() -> new PaymentNotFoundException(id));
    if (!payment.isParticipant(currentUserId)) {
      throw new AccessDeniedException("Only participants of the payment can access it");
    }
    return payment;
  }

  public Page<Payment> getPaymentsForUser(User user, Pageable pageable) {
    return paymentRepository.findByPayer(user, pageable);
  }

  public Page<Payment> getPaymentsForExpense(Expense expense, Pageable pageable) {
    return paymentRepository.findByExpense(expense, pageable);
  }

  public boolean hasPayments(Expense expense, UUID userId) {
    return BigDecimal.ZERO.compareTo(paymentRepository.sumByExpenseAndUserId(expense, userId)) < 0;
  }

  @Transactional
  public Payment createPayment(CreatePaymentCommand command, User currentUser) {
    var expense =
        expenseRepository
            .findByIdForUpdate(command.expenseId())
            .orElseThrow(() -> new ExpenseNotFoundException(command.expenseId()));

    validateIsUserObliged(expense, currentUser.getId());
    validateAmountValue(command.amount());
    validateOverpaidExpense(expense, currentUser, command.amount());

    var payment = new Payment();
    payment.setAmount(command.amount());
    payment.setPayer(currentUser);
    payment.setExpense(expense);
    return paymentRepository.save(payment);
  }

  @Transactional
  public void deletePayment(UUID paymentId, UUID currentUserId) {
    var payment = getPayment(paymentId, currentUserId);
    if (!payment.getPayer().getId().equals(currentUserId)) {
      throw new AccessDeniedException("Only the payer can delete the payment");
    }
    paymentRepository.delete(payment);
  }

  private void validateOverpaidExpense(
      Expense expense, User paymentPayer, BigDecimal paymentAmount) {
    var totalPaid = paymentRepository.sumByExpenseAndUserId(expense, paymentPayer.getId());
    var paymentPayerShare = expense.getExpenseShare(paymentPayer);
    var remainingBalance = paymentPayerShare.getAmount().subtract(totalPaid);
    if (paymentAmount.compareTo(remainingBalance) > 0) {
      throw new PaymentValidationException(
          "Payment amount exceeds the remaining balance of the expense. Remaining balance: "
              + remainingBalance);
    }
  }

  private void validateAmountValue(BigDecimal amount) {
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new PaymentValidationException("Payment amount must be greater than zero");
    }
  }

  private void validateIsUserObliged(Expense expense, UUID userId) {
    if (!expense.isObligatedToPay(userId)) {
      throw new PaymentValidationException(
          "Only users obligated to pay for the expense can record payments");
    }
  }
}
