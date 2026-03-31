package pl.sgorski.expense_splitter.exceptions;

public final class FriendhipStatusNotFoundException extends NotFoundException {
    public FriendhipStatusNotFoundException(String status) {
        super("Friendship status not found: " + status);
    }
}
