package pl.sgorski.expense_splitter.features.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "Login Request", description = "Payload used to authenticate a user.")
public record LoginRequest(
    @Schema(description = "User email address.", example = "john.doe@example.com") @NotBlank @Email
        String email,
    @Schema(description = "User password.", example = "StrongP@ssw0rd!") @NotBlank
        String password) {}
