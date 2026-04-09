package pl.sgorski.expense_splitter.exceptions.authentication;

/**
 * Thrown when oauth2 link mode is required to perform an action <br>
 * This occurs when local user with matching email already exists when trying to log in with an
 * OAuth2 provider
 */
public final class AccountLinkRequiredException extends RuntimeException {
  public AccountLinkRequiredException() {
    super("Account link is required to perform this action");
  }
}
