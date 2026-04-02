package pl.sgorski.expense_splitter.features.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import pl.sgorski.expense_splitter.features.user.dto.contract.PasswordChange;
import pl.sgorski.expense_splitter.validator.password.PasswordMatch;
import pl.sgorski.expense_splitter.validator.password.StrongPassword;

@Schema(name = "Password Change Request", description = "Payload used to change user password.")
@PasswordMatch
public record PasswordChangeRequest(
    @Schema(description = "Current password for authentication.", example = "OldP@ssw0rd!")
        @NotBlank
        String oldPassword,
    @Schema(description = "New password provided by the user.", example = "NewP@ssw0rd!")
        @StrongPassword
        @NotBlank
        String newPassword,
    @Schema(
            description = "Repeated new password for confirmation. Must match newPassword.",
            example = "NewP@ssw0rd!")
        @NotBlank
        String repeatNewPassword)
    implements PasswordChange {}
