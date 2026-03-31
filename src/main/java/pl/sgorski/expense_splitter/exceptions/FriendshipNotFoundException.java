package pl.sgorski.expense_splitter.exceptions;

import java.util.UUID;

public final class FriendshipNotFoundException extends NotFoundException {
    public FriendshipNotFoundException(UUID id) {
        super("Friendship with id: " + id + " not found.");
    }
}
