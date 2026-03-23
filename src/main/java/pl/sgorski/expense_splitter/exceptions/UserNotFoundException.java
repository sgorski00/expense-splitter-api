package pl.sgorski.expense_splitter.exceptions;

import java.util.UUID;

public final class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String email) {
        super("User not found with email: " + email);
    }

    public UserNotFoundException(UUID id) {
        super("User not found with id: " + id.toString());
    }
}
