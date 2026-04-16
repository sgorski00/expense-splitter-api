package pl.sgorski.expense_splitter.features.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(
    name = "Password Reset Request",
    description = "Payload used to generate a onr-time token for password reset.")
public record PasswordResetRequest(
    @Schema(description = "User email address.", example = "john.doe@example.com") @NotBlank @Email
        String email) {}
