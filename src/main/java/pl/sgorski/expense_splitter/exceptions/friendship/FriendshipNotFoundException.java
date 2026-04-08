package pl.sgorski.expense_splitter.exceptions.friendship;

import java.util.UUID;
import pl.sgorski.expense_splitter.exceptions.NotFoundException;

public final class FriendshipNotFoundException extends NotFoundException {
  public FriendshipNotFoundException(UUID id) {
    super("Friendship with id: " + id + " not found.");
  }
}
