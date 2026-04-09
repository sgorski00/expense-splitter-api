package pl.sgorski.expense_splitter.exceptions.friendship;

/**
 * Thrown when an invalid operation is attempted on a friendship relationship. <br>
 * Common cases: - Trying to accept a friendship request as a requester - Trying to invite yourself
 * as a friend
 */
public final class InvalidFriendshipOperationException extends RuntimeException {
  public InvalidFriendshipOperationException(String message) {
    super(message);
  }
}
