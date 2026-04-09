package pl.sgorski.expense_splitter.exceptions.authentication;

/** Thrown when a refresh token is invalid - e.g. expired, malformed, revoked. */
public final class RefreshTokenValidationException extends RuntimeException {
  public RefreshTokenValidationException(String message) {
    super(message);
  }
}
