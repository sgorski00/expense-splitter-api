package pl.sgorski.expense_splitter.features.payment.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.sgorski.expense_splitter.features.expense.domain.Expense;
import pl.sgorski.expense_splitter.features.expense.domain.ExpenseShare;
import pl.sgorski.expense_splitter.features.user.domain.User;

public class PaymentTest {

  private Payment payment;
  private User paymentPayer;
  private User expensePayer;
  private User expenseParticipant;

  @BeforeEach
  void setUp() {
    payment = new Payment();
    paymentPayer = new User();
    paymentPayer.setId(UUID.randomUUID());
    payment.setPayer(paymentPayer);

    var expense = new Expense();
    expense.setId(UUID.randomUUID());
    payment.setExpense(expense);

    expensePayer = new User();
    expensePayer.setId(UUID.randomUUID());
    expense.setPayer(expensePayer);

    var expenseShare = new ExpenseShare();
    expenseParticipant = new User();
    expenseParticipant.setId(UUID.randomUUID());
    expenseShare.setUser(expenseParticipant);
    expense.addShare(expenseShare);
  }

  @Test
  void isParticipant_shouldReturnTrue_whenCurrentUserIsPaymentPayer() {
    var currentUser = paymentPayer;

    var result = payment.isParticipant(currentUser);

    assertTrue(result);
  }

  @Test
  void isParticipant_shouldReturnTrue_whenCurrentUserIsExpensePayer() {
    var currentUser = expensePayer;

    var result = payment.isParticipant(currentUser);

    assertTrue(result);
  }

  @Test
  void isParticipant_shouldReturnTrue_whenCurrentUserIsExpenseParticipant() {
    var currentUser = expenseParticipant;

    var result = payment.isParticipant(currentUser);

    assertTrue(result);
  }

  @Test
  void isParticipant_shouldReturnFalse_whenCurrentUserIsNotParticipant() {
    var currentUser = new User();
    currentUser.setId(UUID.randomUUID());

    var result = payment.isParticipant(currentUser);

    assertFalse(result);
  }
}
