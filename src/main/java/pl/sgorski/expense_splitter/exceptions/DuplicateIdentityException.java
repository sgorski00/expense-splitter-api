package pl.sgorski.expense_splitter.exceptions;

/**
 * Thrown when attempting to add a duplicate identity to a user.
 * <br>
 * This occurs when a user already has an identity linked with the same provider.
 */
public final class DuplicateIdentityException extends RuntimeException {
    public DuplicateIdentityException(String message) {
        super(message);
    }
}

