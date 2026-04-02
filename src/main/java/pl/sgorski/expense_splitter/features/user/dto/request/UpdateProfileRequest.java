package pl.sgorski.expense_splitter.features.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import org.jspecify.annotations.Nullable;

@Schema(
    name = "Update Profile Request",
    description =
        "Payload used to update user profile fields. All fields are optional. If a field is not provided, the corresponding user attribute will remain unchanged.")
public record UpdateProfileRequest(
    @Schema(description = "New email address.", example = "jane.doe@example.com") @Nullable @Email
        String email,
    @Schema(description = "User first name.", example = "Jane") @Nullable String firstName,
    @Schema(description = "User last name.", example = "Doe") @Nullable String lastName) {}
