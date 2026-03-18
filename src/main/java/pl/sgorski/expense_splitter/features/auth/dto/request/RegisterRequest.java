package pl.sgorski.expense_splitter.features.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
//TODO: validate password
@Schema(
        name = "Register Request",
        description = "Payload used to crate a new user account."
)
public record RegisterRequest(
        @Schema(
                description = "New user's email address.",
                example = "john.doe@example.com"
        )
        @NotBlank @Email
        String email,
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
) { }
