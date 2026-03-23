package pl.sgorski.expense_splitter.features.auth.dto.command;

public record LoginUserCommand(
        String email,
        String password
){ }
