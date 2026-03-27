package pl.sgorski.expense_splitter.exceptions;

public final class ProviderNotFoundException extends NotFoundException {
    public ProviderNotFoundException(String role) {
        super("Provider not found: " + role);
    }
}
