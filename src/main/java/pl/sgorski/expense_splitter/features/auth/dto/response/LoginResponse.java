package pl.sgorski.expense_splitter.features.auth.dto.response;

public record LoginResponse(
        String accessToken,
        String refreshToken
) { }
