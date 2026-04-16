package pl.sgorski.expense_splitter.features.expense.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.sgorski.expense_splitter.exceptions.NotFoundException;
import pl.sgorski.expense_splitter.exceptions.expense.ExpenseValidationException;
import pl.sgorski.expense_splitter.features.user.domain.Role;
import pl.sgorski.expense_splitter.features.user.domain.User;

public class ExpenseTest {

  private Expense expense;
  private User payer;
  private User participant1;
  private User participant2;
  private ExpenseShare share1;

  @BeforeEach
  void setUp() {
    payer = new User();
    payer.setId(UUID.randomUUID());
    payer.setEmail("payer@example.com");
    payer.setRole(Role.USER);

    participant1 = new User();
    participant1.setId(UUID.randomUUID());
    participant1.setEmail("participant1@example.com");
    participant1.setRole(Role.USER);
    share1 = new ExpenseShare();
    share1.setUser(participant1);
    share1.setAmount(BigDecimal.valueOf(50.00));

    participant2 = new User();
    participant2.setId(UUID.randomUUID());
    participant2.setEmail("participant2@example.com");
    participant2.setRole(Role.USER);
    var share2 = new ExpenseShare();
    share2.setUser(participant2);
    share2.setAmount(BigDecimal.valueOf(50.00));

    expense = new Expense();
    expense.setId(UUID.randomUUID());
    expense.setTitle("Test Expense");
    expense.setAmountTotal(BigDecimal.valueOf(200.00));
    expense.setPayer(payer);
    expense.setExpenseDate(Instant.now());
    expense.setSplitType(SplitType.EQUAL);
    expense.addShare(share1);
    expense.addShare(share2);
  }

  @Test
  void addShare_shouldAddShareToExpenseAndSetExpenseReference() {
    assertTrue(expense.getShares().contains(share1));
    assertEquals(expense, share1.getExpense());
  }

  @Test
  void addShare_shouldAddMultipleShares() {
    var participant3 = new User();
    participant3.setId(UUID.randomUUID());
    var participant4 = new User();
    participant4.setId(UUID.randomUUID());

    var share3 = new ExpenseShare();
    share3.setUser(participant3);
    share3.setAmount(BigDecimal.valueOf(50.00));
    var share4 = new ExpenseShare();
    share4.setUser(participant4);
    share4.setAmount(BigDecimal.valueOf(50.00));

    expense.addShare(share3);
    expense.addShare(share4);

    assertEquals(4, expense.getShares().size());
    assertTrue(expense.getShares().contains(share3));
    assertTrue(expense.getShares().contains(share4));
  }

  @Test
  void isParticipant_shouldReturnTrue_whenUserIsPayer() {
    assertTrue(expense.isParticipant(payer.getId()));
  }

  @Test
  void isParticipant_shouldReturnTrue_whenUserIsShareholder() {
    assertTrue(expense.isParticipant(participant1.getId()));
  }

  @Test
  void isParticipant_shouldReturnFalse_whenUserIsNotInvolved() {
    var otherUser = new User();
    otherUser.setId(UUID.randomUUID());
    otherUser.setEmail("other@example.com");
    otherUser.setRole(Role.USER);

    assertFalse(expense.isParticipant(otherUser.getId()));
  }

  @Test
  void isParticipant_shouldReturnTrue_whenUserIsPayerAndShareholder() {
    var share = new ExpenseShare();
    share.setUser(payer);
    share.setAmount(BigDecimal.valueOf(50.00));
    expense.addShare(share);

    assertTrue(expense.isParticipant(payer.getId()));
  }

  @Test
  void isObligatedToPay_shouldReturnTrue_whenUserIsShareholder() {
    assertTrue(expense.isObligatedToPay(participant1.getId()));
  }

  @Test
  void isObligatedToPay_shouldReturnFalse_whenUserIsNotShareholder() {
    var otherUser = new User();
    otherUser.setId(UUID.randomUUID());
    otherUser.setEmail("other@example.com");
    otherUser.setRole(Role.USER);

    assertFalse(expense.isObligatedToPay(otherUser.getId()));
  }

  @Test
  void isObligatedToPay_shouldReturnFalse_whenUserIsPayerButNotShareholder() {
    assertFalse(expense.isObligatedToPay(payer.getId()));
  }

  @Test
  void getExpenseShare_shouldReturnShare_whenUserIsShareholder() {
    var result = expense.getExpenseShare(participant1.getId());

    assertNotNull(result);
    assertEquals(participant1, result.getUser());
    assertEquals(BigDecimal.valueOf(50.00), result.getAmount());
    assertEquals(expense, result.getExpense());
  }

  @Test
  void getExpenseShare_shouldThrowNotFoundException_whenUserIsNotShareholder() {
    var otherUser = new User();
    otherUser.setId(UUID.randomUUID());
    otherUser.setEmail("other@example.com");
    otherUser.setRole(Role.USER);

    assertThrows(NotFoundException.class, () -> expense.getExpenseShare(otherUser.getId()));
  }

  @Test
  void getExpenseShare_shouldThrowNotFoundException_whenUserIsPayerButNotShareholder() {
    assertThrows(NotFoundException.class, () -> expense.getExpenseShare(payer.getId()));
  }

  @Test
  void removeParticipant_shouldRemoveShare_whenParticipantExists() {
    var amountBefore = expense.getAmountTotal();
    var shareAmount = share1.getAmount();
    expense.removeParticipant(participant1.getId());

    assertFalse(expense.getShares().contains(share1));
    assertEquals(amountBefore.subtract(shareAmount), expense.getAmountTotal());
  }

  @Test
  void removeParticipant_shouldThrowExpenseValidationException_whenParticipantDoesNotExist() {
    assertThrows(
        ExpenseValidationException.class, () -> expense.removeParticipant(UUID.randomUUID()));
  }

  @Test
  void removeParticipant_shouldThrowExpenseValidationException_whenParticipantIsPayer() {
    assertThrows(ExpenseValidationException.class, () -> expense.removeParticipant(payer.getId()));
  }

  @Test
  void removeParticipant_shouldThrowExpenseValidationException_whenParticipantIsLastExisting() {
    expense.removeParticipant(participant1.getId());
    assertThrows(
        ExpenseValidationException.class, () -> expense.removeParticipant(participant2.getId()));
  }
}
