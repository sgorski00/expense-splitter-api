package pl.sgorski.expense_splitter.features.expense.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.sgorski.expense_splitter.exceptions.NotFoundException;
import pl.sgorski.expense_splitter.features.user.domain.Role;
import pl.sgorski.expense_splitter.features.user.domain.User;

public class ExpenseTest {

  private Expense expense;
  private User payer;
  private User participant;

  @BeforeEach
  void setUp() {
    payer = new User();
    payer.setId(UUID.randomUUID());
    payer.setEmail("payer@example.com");
    payer.setRole(Role.USER);

    participant = new User();
    participant.setId(UUID.randomUUID());
    participant.setEmail("participant@example.com");
    participant.setRole(Role.USER);

    expense = new Expense();
    expense.setId(UUID.randomUUID());
    expense.setTitle("Test Expense");
    expense.setAmountTotal(BigDecimal.valueOf(100.00));
    expense.setPayer(payer);
    expense.setExpenseDate(Instant.now());
    expense.setSplitType(SplitType.EQUAL);
  }

  @Test
  void addShare_shouldAddShareToExpenseAndSetExpenseReference() {
    var share = new ExpenseShare();
    share.setUser(participant);
    share.setAmount(BigDecimal.valueOf(50.00));

    expense.addShare(share);

    assertTrue(expense.getShares().contains(share));
    assertEquals(expense, share.getExpense());
  }

  @Test
  void addShare_shouldAddMultipleShares() {
    var user2 = new User();
    user2.setId(UUID.randomUUID());

    var share1 = new ExpenseShare();
    share1.setUser(participant);
    share1.setAmount(BigDecimal.valueOf(50.00));

    var share2 = new ExpenseShare();
    share2.setUser(user2);
    share2.setAmount(BigDecimal.valueOf(50.00));

    expense.addShare(share1);
    expense.addShare(share2);

    assertEquals(2, expense.getShares().size());
    assertTrue(expense.getShares().contains(share1));
    assertTrue(expense.getShares().contains(share2));
  }

  @Test
  void isParticipant_shouldReturnTrue_whenUserIsPayer() {
    assertTrue(expense.isParticipant(payer));
  }

  @Test
  void isParticipant_shouldReturnTrue_whenUserIsShareholder() {
    var share = new ExpenseShare();
    share.setUser(participant);
    share.setAmount(BigDecimal.valueOf(50.00));
    expense.addShare(share);

    assertTrue(expense.isParticipant(participant));
  }

  @Test
  void isParticipant_shouldReturnFalse_whenUserIsNotInvolved() {
    var otherUser = new User();
    otherUser.setId(UUID.randomUUID());
    otherUser.setEmail("other@example.com");
    otherUser.setRole(Role.USER);

    assertFalse(expense.isParticipant(otherUser));
  }

  @Test
  void isParticipant_shouldReturnTrue_whenUserIsPayerAndShareholder() {
    var share = new ExpenseShare();
    share.setUser(payer);
    share.setAmount(BigDecimal.valueOf(50.00));
    expense.addShare(share);

    assertTrue(expense.isParticipant(payer));
  }

  @Test
  void isObligatedToPay_shouldReturnTrue_whenUserIsShareholder() {
    var share = new ExpenseShare();
    share.setUser(participant);
    share.setAmount(BigDecimal.valueOf(50.00));
    expense.addShare(share);

    assertTrue(expense.isObligatedToPay(participant));
  }

  @Test
  void isObligatedToPay_shouldReturnFalse_whenUserIsNotShareholder() {
    var otherUser = new User();
    otherUser.setId(UUID.randomUUID());
    otherUser.setEmail("other@example.com");
    otherUser.setRole(Role.USER);

    assertFalse(expense.isObligatedToPay(otherUser));
  }

  @Test
  void isObligatedToPay_shouldReturnFalse_whenUserIsPayerButNotShareholder() {
    assertFalse(expense.isObligatedToPay(payer));
  }

  @Test
  void getExpenseShare_shouldReturnShare_whenUserIsShareholder() {
    var share = new ExpenseShare();
    share.setUser(participant);
    share.setAmount(BigDecimal.valueOf(50.00));
    expense.addShare(share);

    var result = expense.getExpenseShare(participant);

    assertNotNull(result);
    assertEquals(participant, result.getUser());
    assertEquals(BigDecimal.valueOf(50.00), result.getAmount());
    assertEquals(expense, result.getExpense());
  }

  @Test
  void getExpenseShare_shouldThrowNotFoundException_whenUserIsNotShareholder() {
    var otherUser = new User();
    otherUser.setId(UUID.randomUUID());
    otherUser.setEmail("other@example.com");
    otherUser.setRole(Role.USER);

    assertThrows(NotFoundException.class, () -> expense.getExpenseShare(otherUser));
  }

  @Test
  void getExpenseShare_shouldThrowNotFoundException_whenUserIsPayerButNotShareholder() {
    assertThrows(NotFoundException.class, () -> expense.getExpenseShare(payer));
  }

  @Test
  void getExpenseShare_shouldReturnCorrectShare_whenMultipleSharesExist() {
    var user2 = new User();
    user2.setId(UUID.randomUUID());
    user2.setEmail("user2@example.com");
    user2.setRole(Role.USER);

    var share1 = new ExpenseShare();
    share1.setUser(participant);
    share1.setAmount(BigDecimal.valueOf(50.00));
    expense.addShare(share1);

    var share2 = new ExpenseShare();
    share2.setUser(user2);
    share2.setAmount(BigDecimal.valueOf(50.00));
    expense.addShare(share2);

    var result = expense.getExpenseShare(participant);

    assertNotNull(result);
    assertEquals(participant, result.getUser());
    assertEquals(BigDecimal.valueOf(50.00), result.getAmount());
  }
}
