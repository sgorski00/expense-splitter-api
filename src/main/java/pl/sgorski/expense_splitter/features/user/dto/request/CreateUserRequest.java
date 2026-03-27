package pl.sgorski.expense_splitter.features.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pl.sgorski.expense_splitter.features.user.domain.Role;
import pl.sgorski.expense_splitter.features.user.dto.contract.PasswordChange;
import pl.sgorski.expense_splitter.validator.password.ValidPassword;

@Schema(
        name = "Create User Request",
        description = "Payload used to create a new user account by an admin."
)
@ValidPassword
public record CreateUserRequest(
        @Schema(
                description = "User email address.",
                example = "john.doe@example.com"
        )
        @NotBlank @Email String email,
        @Schema(
                description = "User role assignment.",
                example = "USER",
                allowableValues = {"USER", "ADMIN"}
        )
        @NotNull Role role,
        @Schema(
                description = "New password provided by the user.",
                example = "StrongP@ssw0rd!"
        )
        @NotBlank String newPassword,
        @Schema(
                description = "Repeated new password for confirmation. Must match newPassword.",
                example = "StrongP@ssw0rd!"
        )
        @NotBlank String repeatNewPassword
) implements PasswordChange {}
