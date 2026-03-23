package pl.sgorski.expense_splitter.exceptions;

public final class PasswordChangeRequiredException extends RuntimeException {
    public PasswordChangeRequiredException() {
        super("Before you can log in, you need to change your password.");
    }
}
