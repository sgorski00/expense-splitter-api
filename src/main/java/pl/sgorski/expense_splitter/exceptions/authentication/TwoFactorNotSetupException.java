package pl.sgorski.expense_splitter.exceptions.authentication;

/** Thrown when attempting to use 2FA (confirm/disable) when it's not set up for the user. */
public final class TwoFactorNotSetupException extends RuntimeException {
  public TwoFactorNotSetupException(String message) {
    super(message);
  }
}
