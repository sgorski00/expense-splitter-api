package pl.sgorski.expense_splitter.exceptions.not_found;

public final class FriendshipStatusNotFoundException extends NotFoundException {
  public FriendshipStatusNotFoundException(String status) {
    super("Friendship status not found: " + status);
  }
}
