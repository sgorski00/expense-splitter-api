package pl.sgorski.expense_splitter.exceptions.not_found;

public final class RefreshTokenNotFoundException extends NotFoundException {
    public RefreshTokenNotFoundException() {
        super("Refresh token not found");
    }
}
