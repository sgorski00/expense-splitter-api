package pl.sgorski.expense_splitter.features.auth.password_reset_token.event;

import java.time.Instant;
import java.util.UUID;

public record PasswordResetRequestEvent(UUID passwordResetToken, Instant createdAt, UUID userId) {}
