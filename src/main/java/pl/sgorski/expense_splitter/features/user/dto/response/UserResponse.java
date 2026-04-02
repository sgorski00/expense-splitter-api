package pl.sgorski.expense_splitter.features.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;
import pl.sgorski.expense_splitter.features.user.domain.Role;

@Schema(name = "User Response", description = "Basic user data returned by API.")
public record UserResponse(
    @Schema(
            description = "User unique identifier.",
            example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
    @Schema(description = "User email address.", example = "john.doe@example.com") String email,
    @Schema(description = "User role assignment.", example = "USER") Role role,
    @Schema(description = "Account creation timestamp.", example = "2026-03-18T10:15:30Z")
        Instant createdAt) {}
