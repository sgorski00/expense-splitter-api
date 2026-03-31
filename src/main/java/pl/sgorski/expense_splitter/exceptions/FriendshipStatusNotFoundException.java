package pl.sgorski.expense_splitter.exceptions;

public final class FriendshipStatusNotFoundException extends NotFoundException {
    public FriendshipStatusNotFoundException(String status) {
        super("Friendship status not found: " + status);
    }
}
