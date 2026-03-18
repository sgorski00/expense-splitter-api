package pl.sgorski.expense_splitter.features.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "Login Response",
        description = "Response returned after successful authentication."
)
public record LoginResponse(
        @Schema(
                description = "JWT access token used to authorize API requests.",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        String accessToken,
        @Schema(
                description = "Refresh token used to obtain a new access token.",
                example = "dGhpcy1pcy1hLXJlZnJlc2gtdG9rZW4="
        )
        String refreshToken
) { }
