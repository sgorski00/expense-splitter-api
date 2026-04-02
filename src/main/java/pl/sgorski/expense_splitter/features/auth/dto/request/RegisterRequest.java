package pl.sgorski.expense_splitter.features.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import pl.sgorski.expense_splitter.features.user.dto.contract.PasswordChange;
import pl.sgorski.expense_splitter.validator.password.ValidPassword;

@Schema(name = "Register Request", description = "Payload used to crate a new user account.")
@ValidPassword
public record RegisterRequest(
    @Schema(description = "New user's email address.", example = "john.doe@example.com")
        @NotBlank
        @Email
        String email,
    @Schema(description = "New user's first name.", example = "John") @NotBlank String firstName,
    @Schema(description = "New user's last name.", example = "Doe") @NotBlank String lastName,
    @Schema(description = "New password provided by the user.", example = "StrongP@ssw0rd!")
        @NotBlank
        String newPassword,
    @Schema(
            description = "Repeated new password for confirmation. Must match newPassword.",
            example = "StrongP@ssw0rd!")
        @NotBlank
        String repeatNewPassword)
    implements PasswordChange {}
