package pl.sgorski.expense_splitter.features.friendship.dto.response;

import org.jspecify.annotations.Nullable;
import pl.sgorski.expense_splitter.features.user.dto.response.UserResponse;

import java.time.Instant;

public record FriendshipResponse(
    Long id,
    UserResponse requester,
    UserResponse recipient,
    String status, //todo: change to enum/entity
    Instant createdAt,
    Instant updatedAt,
    @Nullable Instant deletedAt
) {}
