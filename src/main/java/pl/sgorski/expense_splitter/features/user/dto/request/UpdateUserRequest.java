package pl.sgorski.expense_splitter.features.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import org.jspecify.annotations.Nullable;
import pl.sgorski.expense_splitter.features.user.domain.Role;

@Schema(
    name = "Update User Request",
    description = "Payload used to update an existing user account by an admin.")
public record UpdateUserRequest(
    @Schema(description = "User email address.", example = "john.doe@example.com") @Email
        @Nullable String email,
    @Schema(
            description = "User role assignment.",
            example = "USER",
            allowableValues = {"USER", "ADMIN"})
        @Nullable Role role,
    @Schema(description = "User first name.", example = "Jane") @Nullable String firstName,
    @Schema(description = "User last name.", example = "Doe") @Nullable String lastName) {}
