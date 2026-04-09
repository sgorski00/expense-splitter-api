package pl.sgorski.expense_splitter.exceptions.user;

import pl.sgorski.expense_splitter.exceptions.NotFoundException;

public final class RoleNotFoundException extends NotFoundException {
  public RoleNotFoundException(String role) {
    super("Role not found: " + role);
  }
}
