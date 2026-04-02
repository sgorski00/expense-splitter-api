package pl.sgorski.expense_splitter.exceptions;

/**
 * This occurs when trying to change the status of a friendship in a way that is not allowed by the
 * business rules.
 */
public class FriendshipStatusChangeException extends RuntimeException {
  public FriendshipStatusChangeException() {
    super("Cannot change status of this friendship");
  }
}
