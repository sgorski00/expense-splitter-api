package pl.sgorski.expense_splitter.features.friendship.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FriendshipRequest(
        @NotNull @Positive Long recipientId
) {}
