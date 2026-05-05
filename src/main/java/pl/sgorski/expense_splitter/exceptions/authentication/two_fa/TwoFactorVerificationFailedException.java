package pl.sgorski.expense_splitter.exceptions.authentication.two_fa;

/** Thrown when 2FA code verification fails due to invalid or expired code. */
public final class TwoFactorVerificationFailedException extends RuntimeException {
  public TwoFactorVerificationFailedException(String message) {
    super(message);
  }
}
