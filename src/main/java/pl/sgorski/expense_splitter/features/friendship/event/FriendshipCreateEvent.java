package pl.sgorski.expense_splitter.features.friendship.event;

import java.util.UUID;

public record FriendshipCreateEvent(UUID friendshipId, UUID requesterId, UUID recipientId) {}
