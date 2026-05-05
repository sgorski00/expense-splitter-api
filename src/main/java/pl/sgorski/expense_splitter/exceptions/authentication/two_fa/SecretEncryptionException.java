package pl.sgorski.expense_splitter.exceptions.authentication.two_fa;

public final class SecretEncryptionException extends RuntimeException {
  public SecretEncryptionException(String message, Throwable cause) {
    super(message, cause);
  }
}
