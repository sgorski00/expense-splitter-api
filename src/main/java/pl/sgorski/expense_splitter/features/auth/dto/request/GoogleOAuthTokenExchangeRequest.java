package pl.sgorski.expense_splitter.features.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GoogleOAuthTokenExchangeRequest(@NotBlank String token) {}
