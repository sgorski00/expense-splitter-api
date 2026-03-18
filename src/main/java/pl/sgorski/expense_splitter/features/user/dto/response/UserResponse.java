package pl.sgorski.expense_splitter.features.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(
        name = "User Response",
        description = "Basic user data returned by API."
)
public record UserResponse(
        @Schema(
                description = "User unique identifier.",
                example = "1"
        )
        Long id,
        @Schema(
                description = "User email address.",
                example = "john.doe@example.com"
        )
        String email,
        @Schema(
                description = "User role assignment.",
                example = "USER"
        )
        String role, //todo: change to enum/entity
        @Schema(
                description = "Account creation timestamp.",
                example = "2026-03-18T10:15:30Z"
        )
        Instant createdAt
) { }
