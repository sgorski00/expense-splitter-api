package pl.sgorski.expense_splitter.exceptions;

public final class RoleNotFoundException extends NotFoundException {
    public RoleNotFoundException(String role) {
        super("Role not found: " + role);
    }
}
