package pl.sgorski.expense_splitter.features.user.dto.request;

import jakarta.validation.constraints.Email;
import org.jspecify.annotations.Nullable;

public record UpdateUserRequest(
        @Nullable @Email String email,
        @Nullable String firstName,
        @Nullable String lastName
) { }
