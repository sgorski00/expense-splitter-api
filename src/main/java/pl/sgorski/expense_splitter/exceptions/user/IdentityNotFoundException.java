package pl.sgorski.expense_splitter.exceptions.user;

import pl.sgorski.expense_splitter.exceptions.NotFoundException;

public final class IdentityNotFoundException extends NotFoundException {
  public IdentityNotFoundException(String message) {
    super(message);
  }
}
