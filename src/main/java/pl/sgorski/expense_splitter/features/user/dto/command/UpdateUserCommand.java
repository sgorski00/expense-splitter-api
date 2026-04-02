package pl.sgorski.expense_splitter.features.user.dto.command;

import org.jspecify.annotations.Nullable;
import pl.sgorski.expense_splitter.features.user.domain.Role;

public record UpdateUserCommand(
    @Nullable String email,
    @Nullable String firstName,
    @Nullable String lastName,
    @Nullable Role role) {}
