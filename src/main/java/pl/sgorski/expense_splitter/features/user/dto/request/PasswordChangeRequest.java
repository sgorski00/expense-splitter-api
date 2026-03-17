package pl.sgorski.expense_splitter.features.user.dto.request;

import jakarta.validation.constraints.NotBlank;
//TODO: validate password
public record PasswordChangeRequest(
        @NotBlank String oldPassword,
        @NotBlank String newPassword,
        @NotBlank String repeatNewPassword
){}

