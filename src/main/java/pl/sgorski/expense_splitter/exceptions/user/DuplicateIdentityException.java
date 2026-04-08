package pl.sgorski.expense_splitter.exceptions.user;

/** This occurs when a user already has an identity linked with the same provider. */
public final class DuplicateIdentityException extends RuntimeException {
  public DuplicateIdentityException(String message) {
    super(message);
  }
}
