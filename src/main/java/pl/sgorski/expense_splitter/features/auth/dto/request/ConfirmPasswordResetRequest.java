package pl.sgorski.expense_splitter.features.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import pl.sgorski.expense_splitter.features.user.dto.contract.PasswordChange;
import pl.sgorski.expense_splitter.validator.password.PasswordMatch;
import pl.sgorski.expense_splitter.validator.password.StrongPassword;

@Schema(
    name = "Password Reset Request",
    description = "Payload used to rest user's password after generating password request token.")
@PasswordMatch
public record ConfirmPasswordResetRequest(
    @Schema(
            description =
                "Previously generated password reset token that is assigned to the specified user. Can be obtained in /api/aut/reset-password endpoint.",
            example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull
        UUID token,
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
