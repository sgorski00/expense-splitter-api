package pl.sgorski.expense_splitter.exceptions.not_found;

import java.util.UUID;

public final class FriendshipNotFoundException extends NotFoundException {
    public FriendshipNotFoundException(UUID id) {
        super("Friendship with id: " + id + " not found.");
    }
}
