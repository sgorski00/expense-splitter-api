package pl.sgorski.expense_splitter.exceptions.authentication;

import pl.sgorski.expense_splitter.exceptions.NotFoundException;

public final class PasswordResetTokenNotFoundException extends NotFoundException {
  public PasswordResetTokenNotFoundException() {
    super("Password reset token not found");
  }
}
