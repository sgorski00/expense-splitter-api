package pl.sgorski.expense_splitter.exceptions.not_found;

public final class IdentityNotFoundException extends NotFoundException {
  public IdentityNotFoundException(String message) {
    super(message);
  }
}
