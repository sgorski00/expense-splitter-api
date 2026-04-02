package pl.sgorski.expense_splitter.features.friendship.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import pl.sgorski.expense_splitter.exceptions.FriendshipStatusChangeException;

public class FriendshipTest {

  @Test
  void changeStatus_shouldChangeStatusToAccepted_whenValueIsPending() {
    var status = FriendshipStatus.ACCEPTED;
    var friendship = new Friendship();

    friendship.changeStatus(status);

    assertEquals(status, friendship.getStatus());
  }

  @Test
  void changeStatus_shouldChangeStatusToRejected_whenValueIsPending() {
    var status = FriendshipStatus.REJECTED;
    var friendship = new Friendship();

    friendship.changeStatus(status);

    assertEquals(status, friendship.getStatus());
  }

  @Test
  void changeStatus_shouldThrowFriendshipStatusChangeException_whenStatusIsAccepted() {
    var status = FriendshipStatus.ACCEPTED;
    var friendship = new Friendship();
    friendship.changeStatus(status);

    assertThrows(
        FriendshipStatusChangeException.class,
        () -> friendship.changeStatus(FriendshipStatus.REJECTED));
  }

  @Test
  void changeStatus_shouldThrowFriendshipStatusChangeException_whenStatusIsRejected() {
    var status = FriendshipStatus.REJECTED;
    var friendship = new Friendship();
    friendship.changeStatus(status);

    assertThrows(
        FriendshipStatusChangeException.class,
        () -> friendship.changeStatus(FriendshipStatus.PENDING));
  }
}
