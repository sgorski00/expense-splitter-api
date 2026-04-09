package pl.sgorski.expense_splitter.features.payment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import pl.sgorski.expense_splitter.exceptions.expense.ExpenseNotFoundException;
import pl.sgorski.expense_splitter.exceptions.payment.PaymentNotFoundException;
import pl.sgorski.expense_splitter.exceptions.payment.PaymentValidationException;
import pl.sgorski.expense_splitter.features.expense.domain.Expense;
import pl.sgorski.expense_splitter.features.expense.domain.ExpenseShare;
import pl.sgorski.expense_splitter.features.expense.domain.SplitType;
import pl.sgorski.expense_splitter.features.expense.repository.ExpenseRepository;
import pl.sgorski.expense_splitter.features.payment.domain.Payment;
import pl.sgorski.expense_splitter.features.payment.dto.command.CreatePaymentCommand;
import pl.sgorski.expense_splitter.features.payment.repository.PaymentRepository;
import pl.sgorski.expense_splitter.features.user.domain.Role;
import pl.sgorski.expense_splitter.features.user.domain.User;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

  @Mock private ExpenseRepository expenseRepository;

  @Mock private PaymentRepository paymentRepository;

  @InjectMocks private PaymentService paymentService;

  private User payer;
  private User otherUser;
  private Expense expense;
  private Payment payment;
  private UUID paymentId;
  private UUID expenseId;

  @BeforeEach
  void setUp() {
    paymentId = UUID.randomUUID();
    expenseId = UUID.randomUUID();

    payer = new User();
    payer.setId(UUID.randomUUID());
    payer.setEmail("payer@example.com");
    payer.setRole(Role.USER);

    otherUser = new User();
    otherUser.setId(UUID.randomUUID());
    otherUser.setEmail("other@example.com");
    otherUser.setRole(Role.USER);

    expense = new Expense();
    expense.setId(expenseId);
    expense.setTitle("Test Expense");
    expense.setAmountTotal(BigDecimal.valueOf(100.00));
    expense.setPayer(payer);
    expense.setExpenseDate(Instant.now());
    expense.setSplitType(SplitType.EQUAL);
    var payerShare = new ExpenseShare();
    payerShare.setUser(payer);
    payerShare.setAmount(BigDecimal.valueOf(100.00));
    expense.addShare(payerShare);

    payment = new Payment();
    payment.setId(paymentId);
    payment.setPayer(payer);
    payment.setExpense(expense);
    payment.setAmount(BigDecimal.valueOf(50.00));
  }

  @Test
  void getPayment_shouldReturnPayment_whenPaymentExistsAndUserIsParticipant() {
    when(paymentRepository.findById(eq(paymentId))).thenReturn(Optional.of(payment));

    var result = paymentService.getPayment(paymentId, payer);

    assertNotNull(result);
    assertEquals(payment.getId(), result.getId());
    verify(paymentRepository, times(1)).findById(eq(paymentId));
  }

  @Test
  void getPayment_shouldReturnPayment_whenPaymentExistsAndUserIsExpenseParticipant() {
    payment.setPayer(otherUser);
    when(paymentRepository.findById(eq(paymentId))).thenReturn(Optional.of(payment));

    var result = paymentService.getPayment(paymentId, payer);

    assertNotNull(result);
    assertEquals(payment.getId(), result.getId());
    verify(paymentRepository, times(1)).findById(eq(paymentId));
  }

  @Test
  void getPayment_shouldThrowPaymentNotFoundException_whenPaymentNotFound() {
    when(paymentRepository.findById(eq(paymentId))).thenReturn(Optional.empty());

    assertThrows(PaymentNotFoundException.class, () -> paymentService.getPayment(paymentId, payer));

    verify(paymentRepository, times(1)).findById(eq(paymentId));
  }

  @Test
  void getPayment_shouldThrowAccessDeniedException_whenUserIsNotParticipant() {
    var nonParticipantUser = new User();
    nonParticipantUser.setId(UUID.randomUUID());
    nonParticipantUser.setEmail("nonparticipant@example.com");
    nonParticipantUser.setRole(Role.USER);

    when(paymentRepository.findById(eq(paymentId))).thenReturn(Optional.of(payment));

    assertThrows(
        AccessDeniedException.class,
        () -> paymentService.getPayment(paymentId, nonParticipantUser));

    verify(paymentRepository, times(1)).findById(eq(paymentId));
  }

  @Test
  void getPaymentsForUser_shouldReturnPaymentsPage_whenPaymentsExist() {
    var pageable = Pageable.unpaged();
    var paymentsPage = new PageImpl<>(java.util.List.of(payment));

    when(paymentRepository.findByPayer(eq(payer), eq(pageable))).thenReturn(paymentsPage);

    var result = paymentService.getPaymentsForUser(payer, pageable);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    verify(paymentRepository, times(1)).findByPayer(eq(payer), eq(pageable));
  }

  @Test
  void getPaymentsForUser_shouldReturnEmptyPage_whenNoPaymentsFound() {
    var pageable = Pageable.unpaged();

    when(paymentRepository.findByPayer(eq(payer), eq(pageable))).thenReturn(Page.empty());

    var result = paymentService.getPaymentsForUser(payer, pageable);

    assertNotNull(result);
    assertTrue(result.getContent().isEmpty());
    verify(paymentRepository, times(1)).findByPayer(eq(payer), eq(pageable));
  }

  @Test
  void getPaymentsForExpense_shouldReturnPaymentsPage_whenPaymentsExist() {
    var pageable = Pageable.unpaged();
    var paymentsPage = new PageImpl<>(java.util.List.of(payment));

    when(paymentRepository.findByExpense(eq(expense), eq(pageable))).thenReturn(paymentsPage);

    var result = paymentService.getPaymentsForExpense(expense, pageable);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    verify(paymentRepository, times(1)).findByExpense(eq(expense), eq(pageable));
  }

  @Test
  void getPaymentsForExpense_shouldReturnEmptyPage_whenNoPaymentsFound() {
    var pageable = Pageable.unpaged();

    when(paymentRepository.findByExpense(eq(expense), eq(pageable))).thenReturn(Page.empty());

    var result = paymentService.getPaymentsForExpense(expense, pageable);

    assertNotNull(result);
    assertTrue(result.getContent().isEmpty());
    verify(paymentRepository, times(1)).findByExpense(eq(expense), eq(pageable));
  }

  @Test
  void createPayment_shouldCreatePayment_whenRequestIsValid() {
    var command = new CreatePaymentCommand(expenseId, BigDecimal.valueOf(50.00));

    when(expenseRepository.findByIdForUpdate(eq(expenseId))).thenReturn(Optional.of(expense));
    when(paymentRepository.sumByExpenseAndUser(eq(expense), eq(payer))).thenReturn(BigDecimal.ZERO);
    when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

    var result = paymentService.createPayment(command, payer);

    assertNotNull(result);
    assertEquals(payment.getId(), result.getId());
    assertEquals(payer, result.getPayer());
    assertEquals(expense, result.getExpense());
    assertEquals(command.amount(), result.getAmount());
    verify(expenseRepository, times(1)).findByIdForUpdate(eq(expenseId));
    verify(paymentRepository, times(1)).sumByExpenseAndUser(eq(expense), eq(payer));
    verify(paymentRepository, times(1)).save(any(Payment.class));
  }

  @Test
  void createPayment_shouldCreatePayment_whenPartialPaymentExists() {
    var command = new CreatePaymentCommand(expenseId, BigDecimal.valueOf(50.00));

    when(expenseRepository.findByIdForUpdate(eq(expenseId))).thenReturn(Optional.of(expense));
    when(paymentRepository.sumByExpenseAndUser(eq(expense), eq(payer)))
        .thenReturn(BigDecimal.valueOf(30.00));
    when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

    var result = paymentService.createPayment(command, payer);

    assertNotNull(result);
    verify(expenseRepository, times(1)).findByIdForUpdate(eq(expenseId));
    verify(paymentRepository, times(1)).sumByExpenseAndUser(eq(expense), eq(payer));
    verify(paymentRepository, times(1)).save(any(Payment.class));
  }

  @Test
  void createPayment_shouldThrowExpenseNotFoundException_whenExpenseNotFound() {
    var command = new CreatePaymentCommand(expenseId, BigDecimal.valueOf(75.00));

    when(expenseRepository.findByIdForUpdate(eq(expenseId))).thenReturn(Optional.empty());

    assertThrows(
        ExpenseNotFoundException.class, () -> paymentService.createPayment(command, payer));

    verify(expenseRepository, times(1)).findByIdForUpdate(eq(expenseId));
    verify(paymentRepository, never()).sumByExpenseAndUser(eq(expense), eq(payer));
    verify(paymentRepository, never()).save(any(Payment.class));
  }

  @Test
  void createPayment_shouldThrowPaymentValidationException_whenUserNotObligatedToPay() {
    var command = new CreatePaymentCommand(expenseId, BigDecimal.valueOf(50.00));
    var nonObligatedUser = new User();
    nonObligatedUser.setId(UUID.randomUUID());
    nonObligatedUser.setEmail("nonobligated@example.com");
    nonObligatedUser.setRole(Role.USER);

    when(expenseRepository.findByIdForUpdate(eq(expenseId))).thenReturn(Optional.of(expense));

    assertThrows(
        PaymentValidationException.class,
        () -> paymentService.createPayment(command, nonObligatedUser));

    verify(expenseRepository, times(1)).findByIdForUpdate(eq(expenseId));
    verify(paymentRepository, never()).sumByExpenseAndUser(any(Expense.class), any(User.class));
    verify(paymentRepository, never()).save(any(Payment.class));
  }

  @Test
  void createPayment_shouldThrowPaymentValidationException_whenExceedsRemainingBalance() {
    var command = new CreatePaymentCommand(expenseId, BigDecimal.valueOf(75.00));

    when(expenseRepository.findByIdForUpdate(eq(expenseId))).thenReturn(Optional.of(expense));
    when(paymentRepository.sumByExpenseAndUser(eq(expense), eq(payer)))
        .thenReturn(BigDecimal.valueOf(50.00));

    assertThrows(
        PaymentValidationException.class, () -> paymentService.createPayment(command, payer));

    verify(expenseRepository, times(1)).findByIdForUpdate(eq(expenseId));
    verify(paymentRepository, times(1)).sumByExpenseAndUser(eq(expense), eq(payer));
    verify(paymentRepository, never()).save(any(Payment.class));
  }

  @Test
  void createPayment_shouldThrowPaymentValidationException_whenEqualsExactlyExceedsBalance() {
    var command = new CreatePaymentCommand(expenseId, BigDecimal.valueOf(100.01));

    when(expenseRepository.findByIdForUpdate(eq(expenseId))).thenReturn(Optional.of(expense));
    when(paymentRepository.sumByExpenseAndUser(eq(expense), eq(payer))).thenReturn(BigDecimal.ZERO);

    assertThrows(
        PaymentValidationException.class, () -> paymentService.createPayment(command, payer));

    verify(expenseRepository, times(1)).findByIdForUpdate(eq(expenseId));
    verify(paymentRepository, times(1)).sumByExpenseAndUser(eq(expense), eq(payer));
    verify(paymentRepository, never()).save(any(Payment.class));
  }

  @Test
  void createPayment_shouldCreatePayment_whenPaymentEqualsExactRemainingBalance() {
    var command = new CreatePaymentCommand(expenseId, BigDecimal.valueOf(100.00));

    when(expenseRepository.findByIdForUpdate(eq(expenseId))).thenReturn(Optional.of(expense));
    when(paymentRepository.sumByExpenseAndUser(eq(expense), eq(payer))).thenReturn(BigDecimal.ZERO);
    when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

    var result = paymentService.createPayment(command, payer);

    assertNotNull(result);
    verify(paymentRepository, times(1)).save(any(Payment.class));
  }

  @Test
  void createPayment_shouldThrow_withZeroInitialPayment() {
    var command = new CreatePaymentCommand(expenseId, BigDecimal.ZERO);

    when(expenseRepository.findByIdForUpdate(eq(expenseId))).thenReturn(Optional.of(expense));

    assertThrows(
        PaymentValidationException.class, () -> paymentService.createPayment(command, payer));

    verify(paymentRepository, never()).save(any(Payment.class));
  }

  @Test
  void deletePayment_shouldDeletePayment_whenRequestIsValid() {
    when(paymentRepository.findById(eq(paymentId))).thenReturn(Optional.of(payment));
    doNothing().when(paymentRepository).delete(any(Payment.class));

    paymentService.deletePayment(paymentId, payer);

    verify(paymentRepository, times(1)).findById(eq(paymentId));
    verify(paymentRepository, times(1)).delete(any(Payment.class));
  }

  @Test
  void deletePayment_shouldThrowPaymentNotFoundException_whenPaymentNotFound() {
    when(paymentRepository.findById(eq(paymentId))).thenReturn(Optional.empty());

    assertThrows(
        PaymentNotFoundException.class, () -> paymentService.deletePayment(paymentId, payer));

    verify(paymentRepository, times(1)).findById(eq(paymentId));
    verify(paymentRepository, never()).delete(any(Payment.class));
  }

  @Test
  void deletePayment_shouldThrowAccessDeniedException_whenUserIsNotPayerOfPayment() {
    payment.setPayer(otherUser);
    when(paymentRepository.findById(eq(paymentId))).thenReturn(Optional.of(payment));

    assertThrows(AccessDeniedException.class, () -> paymentService.deletePayment(paymentId, payer));

    verify(paymentRepository, times(1)).findById(eq(paymentId));
    verify(paymentRepository, never()).delete(any(Payment.class));
  }

  @Test
  void deletePayment_shouldThrowAccessDeniedException_whenUserIsNotParticipantOfPayment() {
    var nonParticipantUser = new User();
    nonParticipantUser.setId(UUID.randomUUID());
    nonParticipantUser.setEmail("nonparticipant@example.com");
    nonParticipantUser.setRole(Role.USER);

    when(paymentRepository.findById(eq(paymentId))).thenReturn(Optional.of(payment));

    assertThrows(
        AccessDeniedException.class,
        () -> paymentService.deletePayment(paymentId, nonParticipantUser));

    verify(paymentRepository, times(1)).findById(eq(paymentId));
    verify(paymentRepository, never()).delete(any(Payment.class));
  }
}
