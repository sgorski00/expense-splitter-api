package pl.sgorski.expense_splitter.features.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import pl.sgorski.expense_splitter.features.user.dto.contract.PasswordChange;
import pl.sgorski.expense_splitter.validator.password.ValidPassword;

@Schema(
        name = "Password Set Request",
        description = "Payload used to set OAuth2 user's local password."
)
@ValidPassword
public record PasswordSetRequest(
        @Schema(
                description = "New password provided by the user.",
                example = "NewP@ssw0rd!"
        )
        @NotBlank String newPassword,
        @Schema(
                description = "Repeated new password for confirmation. Must match newPassword.",
                example = "NewP@ssw0rd!"
        )
        @NotBlank String repeatNewPassword
) implements PasswordChange {}

