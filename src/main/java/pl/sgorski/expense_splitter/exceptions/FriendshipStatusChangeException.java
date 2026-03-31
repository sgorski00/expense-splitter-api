package pl.sgorski.expense_splitter.exceptions;

public class FriendshipStatusChangeException extends RuntimeException {
    public FriendshipStatusChangeException() {
        super("Cannot change status of this friendship");
    }
}
