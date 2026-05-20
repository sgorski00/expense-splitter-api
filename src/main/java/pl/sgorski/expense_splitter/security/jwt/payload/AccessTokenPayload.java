package pl.sgorski.expense_splitter.security.jwt.payload;

import java.util.UUID;

public record AccessTokenPayload(
    UUID userId, String email, boolean passwordForChange, boolean twoFactorRequired) {}
