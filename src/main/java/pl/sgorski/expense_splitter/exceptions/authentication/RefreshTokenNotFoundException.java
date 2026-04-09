package pl.sgorski.expense_splitter.exceptions.authentication;

import pl.sgorski.expense_splitter.exceptions.NotFoundException;

public final class RefreshTokenNotFoundException extends NotFoundException {
  public RefreshTokenNotFoundException() {
    super("Refresh token not found");
  }
}
