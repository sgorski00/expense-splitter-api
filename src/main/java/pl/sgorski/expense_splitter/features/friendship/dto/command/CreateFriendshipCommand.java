package pl.sgorski.expense_splitter.features.friendship.dto.command;

import java.util.UUID;

public record CreateFriendshipCommand(
        UUID recipientId
) { }
