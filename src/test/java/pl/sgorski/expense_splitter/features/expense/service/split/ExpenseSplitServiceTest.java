package pl.sgorski.expense_splitter.features.expense.service.split;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.expense_splitter.features.expense.domain.Expense;
import pl.sgorski.expense_splitter.features.expense.domain.ExpenseShare;
import pl.sgorski.expense_splitter.features.expense.domain.SplitType;
import pl.sgorski.expense_splitter.features.expense.dto.command.ParticipantCommand;
import pl.sgorski.expense_splitter.features.user.domain.User;

@ExtendWith(MockitoExtension.class)
public class ExpenseSplitServiceTest {

  @Mock private SplitStrategyFactory factory;

  @Mock private SplitStrategy splitStrategy;

  private ExpenseSplitService service;

  private Expense expense;

  @BeforeEach
  void setUp() {
    service = new ExpenseSplitService(factory);

    expense = new Expense();
    expense.setId(UUID.randomUUID());
    expense.setTitle("Test Expense");
    expense.setAmountTotal(BigDecimal.valueOf(100.00));
    expense.setSplitType(SplitType.EQUAL);
    var payer = new User();
    payer.setId(UUID.randomUUID());
    expense.setPayer(payer);
    expense.setExpenseDate(Instant.now());
  }

  @Test
  void split_shouldReturnShares_whenStrategyIsRegistered() {
    var participantId = UUID.randomUUID();
    var participants = Set.of(new ParticipantCommand(participantId, null, null));
    var expectedShares = Set.of(new ExpenseShare());

    when(factory.get(eq(SplitType.EQUAL))).thenReturn(splitStrategy);
    when(splitStrategy.split(eq(expense.getAmountTotal()), eq(participants)))
        .thenReturn(expectedShares);

    var result = service.split(expense, participants);

    assertNotNull(result);
    assertEquals(expectedShares, result);
  }

  @Test
  void split_shouldThrowIllegalArgumentException_whenStrategyNotRegistered() {
    var participants = Set.of(new ParticipantCommand(UUID.randomUUID(), null, null));

    when(factory.get(eq(SplitType.EQUAL)))
        .thenThrow(new IllegalArgumentException("Unregistered split type"));

    assertThrows(IllegalArgumentException.class, () -> service.split(expense, participants));
  }
}
