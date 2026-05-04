package pl.sgorski.expense_splitter.exceptions.authentication;

/**
 * Thrown when attempting to set up 2FA for a user that already has 2FA enabled or in setup process.
 */
public final class TwoFactorAlreadySetupException extends RuntimeException {
  public TwoFactorAlreadySetupException(String message) {
    super(message);
  }
}
