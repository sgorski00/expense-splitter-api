package pl.sgorski.expense_splitter.exceptions.user;

import pl.sgorski.expense_splitter.exceptions.NotFoundException;

import java.util.UUID;

public final class UserNotFoundException extends NotFoundException {
  public UserNotFoundException(String email) {
    super("User not found with email: " + email);
  }

  public UserNotFoundException(UUID id) {
    super("User not found with id: " + id.toString());
  }
}
