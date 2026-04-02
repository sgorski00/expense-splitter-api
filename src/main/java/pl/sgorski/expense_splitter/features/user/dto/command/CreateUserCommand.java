package pl.sgorski.expense_splitter.features.user.dto.command;

import pl.sgorski.expense_splitter.features.user.domain.Role;

public record CreateUserCommand(
    String email, String firstName, String lastName, Role role, String password) {}
