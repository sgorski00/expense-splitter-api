package pl.sgorski.expense_splitter.features.friendship.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import pl.sgorski.expense_splitter.exceptions.friendship.FriendshipStatusNotFoundException;

public class FriendshipStatusTest {

  @Test
  void fromString_shouldReturnFriendshipStatus_whenValueIsValid() {
    var pending = FriendshipStatus.fromString("Pending");
    var accepted = FriendshipStatus.fromString(" accepted ");
    var rejected = FriendshipStatus.fromString("REJEcted");

    assertEquals(FriendshipStatus.PENDING, pending);
    assertEquals(FriendshipStatus.ACCEPTED, accepted);
    assertEquals(FriendshipStatus.REJECTED, rejected);
  }

  @Test
  void fromString_shouldThrowIllegalArgumentException_whenValueIsInvalid() {
    assertThrows(
        FriendshipStatusNotFoundException.class, () -> FriendshipStatus.fromString("not a status"));
  }
}
