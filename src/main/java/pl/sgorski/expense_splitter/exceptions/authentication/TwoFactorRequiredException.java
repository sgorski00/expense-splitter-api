package pl.sgorski.expense_splitter.exceptions.authentication;

/**
 * Thrown when account is marked with 2fa login flow and trying to visit endpoints that are not
 * whitelisted
 */
public final class TwoFactorRequiredException extends RuntimeException {
  public TwoFactorRequiredException() {
    super(
        "Before you can use the application, you need to verify with your 2FA authentication provider.");
  }

  public TwoFactorRequiredException(String message) {
    super(message);
  }
}
