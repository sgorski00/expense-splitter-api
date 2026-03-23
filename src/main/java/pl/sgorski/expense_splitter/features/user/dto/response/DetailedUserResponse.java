package pl.sgorski.expense_splitter.features.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.jspecify.annotations.Nullable;
import pl.sgorski.expense_splitter.features.user.domain.Role;

import java.time.Instant;
import java.util.UUID;

@Schema(
        name = "Detailed User Response",
        description = "Detailed user data including audit timestamps."
)
public record DetailedUserResponse(
        @Schema(
                description = "User unique identifier.",
                example = "123e4567-e89b-12d3-a456-426614174000"
        )
        UUID id,
        @Schema(
                description = "User email address.",
                example = "john.doe@example.com"
        )
        String email,
        @Schema(
                description = "User first name.",
                example = "John"
        )
        String firstName,
        @Schema(
                description = "User last name.",
                example = "Doe"
        )
        String lastName,
        @Schema(
                description = "User role assignment.",
                example = "USER"
        )
        Role role,
        @Schema(
                description = "Account creation timestamp.",
                example = "2026-03-18T10:15:30Z"
        )
        Instant createdAt,
        @Schema(
                description = "Last update timestamp.",
                example = "2026-03-18T11:00:00Z"
        )
        Instant updatedAt,
        @Schema(
                description = "Soft-delete timestamp when account was removed.",
                example = "2026-03-19T09:00:00Z"
        )
        @Nullable Instant deletedAt
) { }
