package pl.sgorski.expense_splitter.features.friendship.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Schema(
    name = "Friendship Request",
    description = "Payload used to create a friendship invitation.")
public record FriendshipRequest(
    @Schema(
            description = "Identifier of the user receiving the invitation.",
            example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull
        UUID recipientId) {}
