package pl.sgorski.expense_splitter.exceptions;

/** Thrown when a refresh token is invalid - e.g. expired, malformed, revoked. */
public class RefreshTokenValidationException extends RuntimeException {
  public RefreshTokenValidationException(String message) {
    super(message);
  }
}
