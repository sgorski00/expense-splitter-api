package pl.sgorski.expense_splitter.exceptions.friendship;

import pl.sgorski.expense_splitter.exceptions.NotFoundException;

public final class FriendshipStatusNotFoundException extends NotFoundException {
  public FriendshipStatusNotFoundException(String status) {
    super("Friendship status not found: " + status);
  }
}
