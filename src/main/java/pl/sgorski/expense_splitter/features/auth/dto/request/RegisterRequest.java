package pl.sgorski.expense_splitter.features.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.Nullable;
import pl.sgorski.expense_splitter.features.user.dto.contract.PasswordChange;
import pl.sgorski.expense_splitter.validator.password.PasswordMatch;
import pl.sgorski.expense_splitter.validator.password.StrongPassword;
import pl.sgorski.expense_splitter.validator.text.NullOrNotBlank;

@Schema(name = "Register Request", description = "Payload used to crate a new user account.")
@PasswordMatch
public record RegisterRequest(
    @Schema(description = "New user's email address.", example = "john.doe@example.com")
        @NotBlank
        @Email
        String email,
    @Schema(description = "New user's first name.", example = "John") @NullOrNotBlank
        @Nullable String firstName,
    @Schema(description = "New user's last name.", example = "Doe") @NullOrNotBlank
        @Nullable String lastName,
    @Schema(description = "New password provided by the user.", example = "StrongP@ssw0rd!")
        @StrongPassword
        @NotBlank
        String newPassword,
    @Schema(
            description = "Repeated new password for confirmation. Must match newPassword.",
            example = "StrongP@ssw0rd!")
        @NotBlank
        String repeatNewPassword)
    implements PasswordChange {}
