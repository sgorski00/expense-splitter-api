package pl.sgorski.expense_splitter.features.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(
    name = "Simple User Response",
    description =
        "Simple user data returned by API. Good for listing users or showing basic profile info.")
public record SimpleUserResponse(
    @Schema(
            description = "User unique identifier.",
            example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
    @Schema(
            description = "User email address. Depends on the implementation, but can be encrypted",
            example = "joh*****@example.com")
        String email,
    @Schema(description = "User first name.", example = "John") String firstName,
    @Schema(description = "User last name.", example = "Doe") String lastName) {}
