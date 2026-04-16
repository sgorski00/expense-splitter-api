package pl.sgorski.expense_splitter.features.auth.dto.command;

import java.util.UUID;

public record ConfirmPasswordResetCommand(UUID token, String newPassword) {}
