package pl.sgorski.expense_splitter.features.payment.service;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.exceptions.payment.PaymentNotFoundException;
import pl.sgorski.expense_splitter.exceptions.payment.PaymentValidationException;
import pl.sgorski.expense_splitter.features.expense.domain.Expense;
import pl.sgorski.expense_splitter.features.expense.service.ExpenseService;
import pl.sgorski.expense_splitter.features.payment.domain.Payment;
import pl.sgorski.expense_splitter.features.payment.dto.command.CreatePaymentCommand;
import pl.sgorski.expense_splitter.features.payment.repository.PaymentRepository;
import pl.sgorski.expense_splitter.features.user.domain.User;

@Service
@RequiredArgsConstructor
public class PaymentService {

  private final ExpenseService expenseService;
  private final PaymentRepository paymentRepository;

  public Payment getPayment(UUID id, User currentUser) {
    var payment =
        paymentRepository.findById(id).orElseThrow(() -> new PaymentNotFoundException(id));
    if (!payment.isParticipant(currentUser)) {
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

  @Transactional
  public Payment createPayment(CreatePaymentCommand command, User currentUser) {
    var expense = expenseService.getExpense(command.expenseId(), currentUser);
    validateOverpaidExpense(expense, command.amount());
    var payment = new Payment();
    payment.setAmount(command.amount());
    payment.setPayer(currentUser);
    payment.setExpense(expense);
    return paymentRepository.save(payment);
  }

  @Transactional
  public void deletePayment(UUID paymentId, User currentUser) {
    var payment = getPayment(paymentId, currentUser);
    if (!payment.getPayer().equals(currentUser)) {
      throw new AccessDeniedException("Only the payer can delete the payment");
    }
    paymentRepository.delete(payment);
  }

  private void validateOverpaidExpense(Expense expense, BigDecimal paymentAmount) {
    var totalPaid = paymentRepository.sumByExpense(expense);
    var remainingBalance = expense.getAmountTotal().subtract(totalPaid);
    if (paymentAmount.compareTo(remainingBalance) > 0) {
      throw new PaymentValidationException(
          "Payment amount exceeds the remaining balance of the expense");
    }
  }
}
