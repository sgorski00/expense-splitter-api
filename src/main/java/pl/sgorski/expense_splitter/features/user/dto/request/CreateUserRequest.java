package pl.sgorski.expense_splitter.features.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

//TODO: validate password
public record CreateUserRequest(
        @NotBlank @Email String email,
        @NotNull String role, //todo: change to enum/entity
        @NotBlank String newPassword,
        @NotBlank String repeatNewPassword
) {}
