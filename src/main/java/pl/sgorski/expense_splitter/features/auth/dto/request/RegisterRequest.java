package pl.sgorski.expense_splitter.features.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
//TODO: validate password
public record RegisterRequest(
        @NotBlank @Email String email,
        @NotBlank String newPassword,
        @NotBlank String repeatNewPassword
) { }
