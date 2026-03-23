package pl.sgorski.expense_splitter.exceptions;

import java.util.UUID;

public final class IdentityNotFoundException extends NotFoundException {
    public IdentityNotFoundException(String message) {
        super(message);
    }
}
