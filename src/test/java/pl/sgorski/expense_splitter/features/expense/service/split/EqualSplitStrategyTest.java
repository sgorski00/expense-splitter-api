package pl.sgorski.expense_splitter.features.expense.service.split;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.expense_splitter.features.expense.domain.ExpenseShare;
import pl.sgorski.expense_splitter.features.expense.dto.command.ParticipantCommand;
import pl.sgorski.expense_splitter.features.expense.service.split.impl.EqualSplitStrategy;
import pl.sgorski.expense_splitter.features.user.domain.Role;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class EqualSplitStrategyTest {

  @Mock private UserService userService;

  private EqualSplitStrategy strategy;

  @BeforeEach
  void setUp() {
    strategy = new EqualSplitStrategy(userService);
  }

  @Test
  void split_shouldDivideEvenlyBetweenParticipants_whenAmountIsDivisible() {
    var user1 = createUser(UUID.randomUUID());
    var user2 = createUser(UUID.randomUUID());
    var user3 = createUser(UUID.randomUUID());

    var participants =
        Set.of(
            new ParticipantCommand(user1.getId(), null, null),
            new ParticipantCommand(user2.getId(), null, null),
            new ParticipantCommand(user3.getId(), null, null));

    when(userService.getUsers(anySet())).thenReturn(List.of(user1, user2, user3));

    var result = strategy.split(BigDecimal.valueOf(300.00), participants);

    assertNotNull(result);
    assertEquals(3, result.size());

    var shares = result.stream().toList();
    shares.forEach(
        share -> assertEquals(0, BigDecimal.valueOf(100.00).compareTo(share.getAmount())));
  }

  @Test
  void split_shouldReturnSharesWithRoundedAmount_whenAmountHasRemainder() {
    var user1 = createUser(UUID.randomUUID());
    var user2 = createUser(UUID.randomUUID());
    var user3 = createUser(UUID.randomUUID());

    var participants =
        Set.of(
            new ParticipantCommand(user1.getId(), null, null),
            new ParticipantCommand(user2.getId(), null, null),
            new ParticipantCommand(user3.getId(), null, null));

    when(userService.getUsers(anySet())).thenReturn(List.of(user1, user2, user3));

    var result = strategy.split(BigDecimal.valueOf(10.00), participants);

    assertNotNull(result);
    assertEquals(3, result.size());

    var shares = result.stream().toList();
    shares.forEach(share -> assertEquals(0, BigDecimal.valueOf(3.33).compareTo(share.getAmount())));
  }

  @Test
  void split_shouldAssignUsersCorrectlyToShares() {
    var user1 = createUser(UUID.randomUUID());
    var user2 = createUser(UUID.randomUUID());

    var participants =
        Set.of(
            new ParticipantCommand(user1.getId(), null, null),
            new ParticipantCommand(user2.getId(), null, null));

    when(userService.getUsers(anySet())).thenReturn(List.of(user1, user2));

    var result = strategy.split(BigDecimal.valueOf(100.00), participants);

    var users = result.stream().map(ExpenseShare::getUser).toList();
    assertTrue(users.contains(user1));
    assertTrue(users.contains(user2));
  }

  @Test
  void split_shouldReturnSingleShare_whenOnlyOneParticipant() {
    var user = createUser(UUID.randomUUID());
    var participants = Set.of(new ParticipantCommand(user.getId(), null, null));

    when(userService.getUsers(anySet())).thenReturn(List.of(user));

    var result = strategy.split(BigDecimal.valueOf(100.00), participants);

    assertEquals(1, result.size());
    var share = result.iterator().next();
    assertEquals(0, BigDecimal.valueOf(100.00).compareTo(share.getAmount()));
    assertEquals(user, share.getUser());
  }

  @Test
  void split_shouldReturnExpenseShareSet_withCorrectSize() {
    var user1 = createUser(UUID.randomUUID());
    var user2 = createUser(UUID.randomUUID());
    var user3 = createUser(UUID.randomUUID());
    var user4 = createUser(UUID.randomUUID());

    var participants =
        Set.of(
            new ParticipantCommand(user1.getId(), null, null),
            new ParticipantCommand(user2.getId(), null, null),
            new ParticipantCommand(user3.getId(), null, null),
            new ParticipantCommand(user4.getId(), null, null));

    when(userService.getUsers(anySet())).thenReturn(List.of(user1, user2, user3, user4));

    var result = strategy.split(BigDecimal.valueOf(400.00), participants);

    assertEquals(4, result.size());
  }

  private User createUser(UUID id) {
    var user = new User();
    user.setId(id);
    user.setEmail("user+" + id + "@example.com");
    user.setRole(Role.USER);
    return user;
  }
}
