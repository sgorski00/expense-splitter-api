package pl.sgorski.expense_splitter.features.auth.dto.command;

public record RegisterUserCommand(
    String email,
    String firstName,
    String lastName,
    String newPassword,
    String repeatNewPassword) {}
