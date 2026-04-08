package pl.sgorski.expense_splitter.exceptions.authentication;

/**
 * Thrown when an account linking operation fails due to invalid state. <br>
 * Common cases: - Account is already linked to another user - User ID is missing when attempting to
 * link an account
 */
public final class AccountLinkingException extends RuntimeException {
  public AccountLinkingException(String message) {
    super(message);
  }
}
