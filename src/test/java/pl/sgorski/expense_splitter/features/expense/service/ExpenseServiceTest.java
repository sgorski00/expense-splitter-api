package pl.sgorski.expense_splitter.features.expense.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import pl.sgorski.expense_splitter.exceptions.expense.ExpenseNotFoundException;
import pl.sgorski.expense_splitter.exceptions.expense.ExpenseValidationException;
import pl.sgorski.expense_splitter.features.expense.domain.Expense;
import pl.sgorski.expense_splitter.features.expense.domain.SplitType;
import pl.sgorski.expense_splitter.features.expense.dto.command.CreateExpenseCommand;
import pl.sgorski.expense_splitter.features.expense.dto.command.ParticipantCommand;
import pl.sgorski.expense_splitter.features.expense.dto.command.UpdateExpenseCommand;
import pl.sgorski.expense_splitter.features.expense.dto.filter.ExpenseRole;
import pl.sgorski.expense_splitter.features.expense.mapper.ExpenseMapper;
import pl.sgorski.expense_splitter.features.expense.repository.ExpenseRepository;
import pl.sgorski.expense_splitter.features.expense.service.split.ExpenseSplitService;
import pl.sgorski.expense_splitter.features.friendship.service.FriendshipService;
import pl.sgorski.expense_splitter.features.payment.service.PaymentService;
import pl.sgorski.expense_splitter.features.user.domain.Role;
import pl.sgorski.expense_splitter.features.user.domain.User;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceTest {

  @Mock private ExpenseSplitService splitService;

  @Mock private ExpenseRepository expenseRepository;

  @Mock private FriendshipService friendshipService;
  @Mock private PaymentService paymentService;

  private ExpenseService expenseService;

  private User payer;
  private Expense expense;
  private UUID expenseId;

  @BeforeEach
  void setUp() {
    var expenseMapper = Mappers.getMapper(ExpenseMapper.class);
    expenseService =
        new ExpenseService(
            splitService, expenseRepository, expenseMapper, friendshipService, paymentService);

    expenseId = UUID.randomUUID();
    payer = new User();
    payer.setId(UUID.randomUUID());
    payer.setEmail("payer@example.com");
    payer.setRole(Role.USER);

    var participant = new User();
    participant.setId(UUID.randomUUID());
    participant.setEmail("participant@example.com");
    participant.setRole(Role.USER);

    expense = new Expense();
    expense.setId(expenseId);
    expense.setTitle("Test Expense");
    expense.setDescription("Test Description");
    expense.setAmountTotal(BigDecimal.valueOf(100.00));
    expense.setPayer(payer);
    expense.setExpenseDate(Instant.now());
    expense.setSplitType(SplitType.EQUAL);
  }

  @Test
  void createExpense_shouldCreateExpense_whenRequestIsValid() {
    var participantId = UUID.randomUUID();
    var command =
        new CreateExpenseCommand(
            "Dinner",
            "Team dinner",
            BigDecimal.valueOf(100.00),
            SplitType.EQUAL,
            Set.of(new ParticipantCommand(participantId, null, null)),
            Instant.now());

    when(friendshipService.areFriends(eq(payer), anyList())).thenReturn(true);
    when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
    when(splitService.split(any(Expense.class), anySet())).thenReturn(Set.of());

    var result = expenseService.createExpense(payer, command);

    assertNotNull(result);
    assertEquals(expense.getId(), result.getId());
    verify(friendshipService, times(1)).areFriends(eq(payer), anyList());
    verify(splitService, times(1)).split(any(Expense.class), anySet());
    verify(expenseRepository, times(1)).save(any(Expense.class));
  }

  @Test
  void createExpense_shouldThrowExpenseValidationException_whenParticipantsAreEmpty() {
    var command =
        new CreateExpenseCommand(
            "Dinner",
            "Team dinner",
            BigDecimal.valueOf(100.00),
            SplitType.EQUAL,
            Set.of(),
            Instant.now());

    assertThrows(
        ExpenseValidationException.class, () -> expenseService.createExpense(payer, command));

    verify(friendshipService, never()).areFriends(any(User.class), anyList());
    verify(splitService, never()).split(any(Expense.class), anySet());
    verify(expenseRepository, never()).save(any(Expense.class));
  }

  @Test
  void createExpense_shouldThrowExpenseValidationException_whenParticipantsAreNull() {
    var command =
        new CreateExpenseCommand(
            "Dinner",
            "Team dinner",
            BigDecimal.valueOf(100.00),
            SplitType.EQUAL,
            null,
            Instant.now());

    assertThrows(
        ExpenseValidationException.class, () -> expenseService.createExpense(payer, command));

    verify(friendshipService, never()).areFriends(any(User.class), anyList());
    verify(splitService, never()).split(any(Expense.class), anySet());
    verify(expenseRepository, never()).save(any(Expense.class));
  }

  @Test
  void createExpense_shouldThrowExpenseValidationException_whenParticipantsAreNotFriends() {
    var participantId = UUID.randomUUID();
    var command =
        new CreateExpenseCommand(
            "Dinner",
            "Team dinner",
            BigDecimal.valueOf(100.00),
            SplitType.EQUAL,
            Set.of(new ParticipantCommand(participantId, null, null)),
            Instant.now());

    when(friendshipService.areFriends(eq(payer), anyList())).thenReturn(false);

    assertThrows(
        ExpenseValidationException.class, () -> expenseService.createExpense(payer, command));

    verify(friendshipService, times(1)).areFriends(eq(payer), anyList());
    verify(splitService, never()).split(any(Expense.class), anySet());
    verify(expenseRepository, never()).save(any(Expense.class));
  }

  @Test
  void createExpense_shouldCreateExpense_withMultipleParticipants() {
    var participantId1 = UUID.randomUUID();
    var participantId2 = UUID.randomUUID();
    var command =
        new CreateExpenseCommand(
            "Dinner",
            "Team dinner",
            BigDecimal.valueOf(100.00),
            SplitType.EQUAL,
            Set.of(
                new ParticipantCommand(participantId1, null, null),
                new ParticipantCommand(participantId2, null, null)),
            Instant.now());

    when(friendshipService.areFriends(eq(payer), anyList())).thenReturn(true);
    when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
    when(splitService.split(any(Expense.class), anySet())).thenReturn(Set.of());

    var result = expenseService.createExpense(payer, command);

    assertNotNull(result);
    verify(friendshipService, times(1)).areFriends(eq(payer), anyList());
    verify(splitService, times(1)).split(any(Expense.class), anySet());
    verify(expenseRepository, times(1)).save(any(Expense.class));
  }

  @Test
  void getExpense_shouldReturnExpense_whenExpenseExistsAndUserIsParticipant() {
    when(expenseRepository.findById(eq(expenseId))).thenReturn(Optional.of(expense));

    var result = expenseService.getExpense(expenseId, payer.getId());

    assertNotNull(result);
    assertEquals(expense.getId(), result.getId());
    verify(expenseRepository, times(1)).findById(eq(expenseId));
  }

  @Test
  void getExpense_shouldThrowExpenseNotFoundException_whenExpenseNotFound() {
    when(expenseRepository.findById(eq(expenseId))).thenReturn(Optional.empty());

    assertThrows(
        ExpenseNotFoundException.class, () -> expenseService.getExpense(expenseId, payer.getId()));

    verify(expenseRepository, times(1)).findById(eq(expenseId));
  }

  @Test
  void getExpense_shouldThrowExpenseNotFoundException_whenUserIsNotParticipant() {
    var otherUser = new User();
    otherUser.setId(UUID.randomUUID());
    otherUser.setEmail("other@example.com");
    otherUser.setRole(Role.USER);

    when(expenseRepository.findById(eq(expenseId))).thenReturn(Optional.of(expense));

    assertThrows(
        ExpenseNotFoundException.class,
        () -> expenseService.getExpense(expenseId, otherUser.getId()));

    verify(expenseRepository, times(1)).findById(eq(expenseId));
  }

  @Test
  void getExpenses_shouldReturnParticipantExpenses_whenRoleIsParticipant() {
    var pageable = Pageable.unpaged();
    var expensesPage = new PageImpl<>(List.of(expense));

    when(expenseRepository.findByParticipant(eq(payer), eq(pageable))).thenReturn(expensesPage);

    var result = expenseService.getExpenses(payer, ExpenseRole.PARTICIPANT, pageable);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    verify(expenseRepository, times(1)).findByParticipant(eq(payer), eq(pageable));
    verifyNoMoreInteractions(expenseRepository);
  }

  @Test
  void getExpenses_shouldReturnPayerExpenses_whenRoleIsPayer() {
    var pageable = Pageable.unpaged();
    var expensesPage = new PageImpl<>(List.of(expense));

    when(expenseRepository.findByPayer(eq(payer), eq(pageable))).thenReturn(expensesPage);

    var result = expenseService.getExpenses(payer, ExpenseRole.PAYER, pageable);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    verify(expenseRepository, times(1)).findByPayer(eq(payer), eq(pageable));
    verifyNoMoreInteractions(expenseRepository);
  }

  @Test
  void getExpenses_shouldReturnAllExpenses_whenRoleIsNull() {
    var pageable = Pageable.unpaged();
    var expensesPage = new PageImpl<>(List.of(expense));

    when(expenseRepository.findByUser(eq(payer), eq(pageable))).thenReturn(expensesPage);

    var result = expenseService.getExpenses(payer, null, pageable);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    verify(expenseRepository, times(1)).findByUser(eq(payer), eq(pageable));
    verifyNoMoreInteractions(expenseRepository);
  }

  @Test
  void getExpenses_shouldReturnEmptyPage_whenNoExpensesFound() {
    var pageable = Pageable.unpaged();

    when(expenseRepository.findByUser(eq(payer), eq(pageable))).thenReturn(Page.empty());

    var result = expenseService.getExpenses(payer, null, pageable);

    assertNotNull(result);
    assertTrue(result.getContent().isEmpty());
    verify(expenseRepository, times(1)).findByUser(eq(payer), eq(pageable));
  }

  @Test
  void updateExpense_shouldUpdateExpense_whenRequestIsValid() {
    var command = new UpdateExpenseCommand("Updated Title", "Updated Description", Instant.now());

    when(expenseRepository.findById(eq(expenseId))).thenReturn(Optional.of(expense));
    when(expenseRepository.save(any(Expense.class))).thenReturn(expense);

    var result = expenseService.updateExpense(expenseId, payer, command);

    assertNotNull(result);
    verify(expenseRepository, times(1)).findById(eq(expenseId));
    verify(expenseRepository, times(1)).save(any(Expense.class));
  }

  @Test
  void updateExpense_shouldThrowExpenseNotFoundException_whenExpenseNotFound() {
    var command = new UpdateExpenseCommand("Updated Title", "Updated Description", Instant.now());

    when(expenseRepository.findById(eq(expenseId))).thenReturn(Optional.empty());

    assertThrows(
        ExpenseNotFoundException.class,
        () -> expenseService.updateExpense(expenseId, payer, command));

    verify(expenseRepository, times(1)).findById(eq(expenseId));
    verify(expenseRepository, never()).save(any(Expense.class));
  }

  @Test
  void updateExpense_shouldThrowExpenseNotFoundException_whenUserIsNotPayer() {
    var command = new UpdateExpenseCommand("Updated Title", "Updated Description", Instant.now());
    var otherUser = new User();
    otherUser.setId(UUID.randomUUID());
    otherUser.setEmail("other@example.com");
    otherUser.setRole(Role.USER);

    when(expenseRepository.findById(eq(expenseId))).thenReturn(Optional.of(expense));

    assertThrows(
        ExpenseNotFoundException.class,
        () -> expenseService.updateExpense(expenseId, otherUser, command));

    verify(expenseRepository, times(1)).findById(eq(expenseId));
    verify(expenseRepository, never()).save(any(Expense.class));
  }

  @Test
  void deleteExpense_shouldDeleteExpense_whenRequestIsValid() {
    when(expenseRepository.findById(eq(expenseId))).thenReturn(Optional.of(expense));
    doNothing().when(expenseRepository).delete(any(Expense.class));

    expenseService.deleteExpense(expenseId, payer);

    verify(expenseRepository, times(1)).findById(eq(expenseId));
    verify(expenseRepository, times(1)).delete(any(Expense.class));
  }

  @Test
  void deleteExpense_shouldThrowExpenseNotFoundException_whenExpenseNotFound() {
    when(expenseRepository.findById(eq(expenseId))).thenReturn(Optional.empty());

    assertThrows(
        ExpenseNotFoundException.class, () -> expenseService.deleteExpense(expenseId, payer));

    verify(expenseRepository, times(1)).findById(eq(expenseId));
    verify(expenseRepository, never()).delete(any(Expense.class));
  }

  @Test
  void deleteExpense_shouldThrowExpenseNotFoundException_whenUserIsNotPayer() {
    var otherUser = new User();
    otherUser.setId(UUID.randomUUID());
    otherUser.setEmail("other@example.com");
    otherUser.setRole(Role.USER);

    when(expenseRepository.findById(eq(expenseId))).thenReturn(Optional.of(expense));

    assertThrows(
        ExpenseNotFoundException.class, () -> expenseService.deleteExpense(expenseId, otherUser));

    verify(expenseRepository, times(1)).findById(eq(expenseId));
    verify(expenseRepository, never()).delete(any(Expense.class));
  }
}
