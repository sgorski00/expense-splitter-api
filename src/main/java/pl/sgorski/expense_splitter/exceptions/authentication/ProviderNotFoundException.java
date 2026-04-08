package pl.sgorski.expense_splitter.exceptions.authentication;

import pl.sgorski.expense_splitter.exceptions.NotFoundException;

public final class ProviderNotFoundException extends NotFoundException {
  public ProviderNotFoundException(String role) {
    super("Provider not found: " + role);
  }
}
