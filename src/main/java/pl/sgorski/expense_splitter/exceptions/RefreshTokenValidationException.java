package pl.sgorski.expense_splitter.exceptions;

public class RefreshTokenValidationException extends RuntimeException {
    public RefreshTokenValidationException(String message) {
        super(message);
    }
}
