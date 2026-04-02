package pl.sgorski.expense_splitter.features.friendship.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import pl.sgorski.expense_splitter.features.friendship.domain.FriendshipStatus;
import pl.sgorski.expense_splitter.features.user.dto.response.UserResponse;

@Schema(
    name = "Friendship Response",
    description = "Friendship relation details between two users.")
public record FriendshipResponse(
    @Schema(
            description = "Friendship relation unique identifier.",
            example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
    @Schema(description = "User who sent the friendship invitation.") UserResponse requester,
    @Schema(description = "User who received the friendship invitation.") UserResponse recipient,
    @Schema(description = "Current friendship status.", example = "PENDING")
        FriendshipStatus status,
    @Schema(description = "Creation timestamp.", example = "2026-03-18T10:15:30Z")
        Instant createdAt,
    @Schema(description = "Last update timestamp.", example = "2026-03-18T11:00:00Z")
        Instant updatedAt,
    @Schema(
            description = "Soft delete timestamp. Null if not deleted.",
            example = "2026-03-19T12:30:00Z")
        @Nullable Instant deletedAt) {}
