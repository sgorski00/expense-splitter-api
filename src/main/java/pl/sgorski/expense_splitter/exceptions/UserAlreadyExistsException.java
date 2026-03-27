package pl.sgorski.expense_splitter.exceptions;

public final class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("Given user already exists");
    }
}
