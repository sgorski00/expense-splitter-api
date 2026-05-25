package pl.sgorski.expense_splitter.security.access_token;

import java.util.UUID;

public record AccessTokenPayload(
    UUID userId, String email, boolean passwordForChange, boolean twoFactorRequired) {}
