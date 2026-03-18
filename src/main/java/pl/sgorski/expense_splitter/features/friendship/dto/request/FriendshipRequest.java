package pl.sgorski.expense_splitter.features.friendship.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(
        name = "Friendship Request",
        description = "Payload used to create a friendship invitation."
)
public record FriendshipRequest(
        @Schema(
                description = "Identifier of the user receiving the invitation.",
                example = "42"
        )
        @NotNull @Positive Long recipientId
) {}
