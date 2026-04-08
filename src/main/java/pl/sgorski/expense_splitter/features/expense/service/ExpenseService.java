package pl.sgorski.expense_splitter.features.expense.service;

import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pl.sgorski.expense_splitter.exceptions.ExpenseValidationException;
import pl.sgorski.expense_splitter.exceptions.not_found.ExpenseNotFoundException;
import pl.sgorski.expense_splitter.features.expense.domain.Expense;
import pl.sgorski.expense_splitter.features.expense.dto.command.CreateExpenseCommand;
import pl.sgorski.expense_splitter.features.expense.dto.command.ParticipantCommand;
import pl.sgorski.expense_splitter.features.expense.dto.command.UpdateExpenseCommand;
import pl.sgorski.expense_splitter.features.expense.dto.filter.ExpenseRole;
import pl.sgorski.expense_splitter.features.expense.mapper.ExpenseMapper;
import pl.sgorski.expense_splitter.features.expense.repository.ExpenseRepository;
import pl.sgorski.expense_splitter.features.expense.service.split.ExpenseSplitService;
import pl.sgorski.expense_splitter.features.friendship.service.FriendshipService;
import pl.sgorski.expense_splitter.features.user.domain.User;

@Service
@RequiredArgsConstructor
public class ExpenseService {

  private final ExpenseSplitService splitService;
  private final ExpenseRepository expenseRepository;
  private final ExpenseMapper mapper;
  private final FriendshipService friendshipService;

  @Transactional
  public Expense createExpense(User user, CreateExpenseCommand command) {
    var participants = command.participants();
    if (CollectionUtils.isEmpty(participants)) {
      throw new ExpenseValidationException(
          "At least one participant of the expense must be specified.");
    }

    var participantIds = participants.stream().map(ParticipantCommand::userId).toList();
    if (!friendshipService.areFriends(user, participantIds)) {
      throw new ExpenseValidationException(
          "All expense participants must be friends with the payer.");
    }

    var expense = mapper.toEntity(command);
    expense.setPayer(user);
    var shares = splitService.split(expense, participants);
    shares.forEach(expense::addShare);
    return expenseRepository.save(expense);
  }

  public Expense getExpense(UUID id, User user) {
    return expenseRepository
        .findById(id)
        .filter(expense -> expense.isParticipant(user))
        .orElseThrow(() -> new ExpenseNotFoundException(id));
  }

  public Page<Expense> getExpenses(User user, @Nullable ExpenseRole role, Pageable pageable) {
    return switch (role) {
      case PARTICIPANT -> expenseRepository.findByParticipant(user, pageable);
      case PAYER -> expenseRepository.findByPayer(user, pageable);
      case null -> expenseRepository.findByUser(user, pageable);
    };
  }

  @Transactional
  public Expense updateExpense(UUID id, User user, UpdateExpenseCommand command) {
    var expense = getExpenseAsPayer(id, user);
    mapper.updateExpense(command, expense);
    return expenseRepository.save(expense);
  }

  @Transactional
  public void deleteExpense(UUID id, User user) {
    var expense = getExpenseAsPayer(id, user);
    expenseRepository.delete(expense);
  }

  private Expense getExpenseAsPayer(UUID id, User user) {
    return expenseRepository
        .findById(id)
        .filter(exp -> exp.getPayer().equals(user))
        .orElseThrow(() -> new ExpenseNotFoundException(id));
  }
}
