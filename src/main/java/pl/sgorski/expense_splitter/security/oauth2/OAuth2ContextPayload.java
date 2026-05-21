package pl.sgorski.expense_splitter.security.oauth2;

import java.util.UUID;

public record OAuth2ContextPayload(UUID userId, OAuth2Mode mode) {}
