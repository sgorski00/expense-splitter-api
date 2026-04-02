package pl.sgorski.expense_splitter.features.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(
        name = "Login Response",
        description = "Response returned after successful authentication. Contains JWT access token and refresh token."
)
public record LoginResponse(
        @Schema(
                description = "JWT access token used to authorize API requests. Valid for short period (default 1 hour).",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJlbWFpbCI6InVzZXJAZXhhbXBsZS5jb20iLCJyb2xlcyI6WyJST0xFX1VTRVIiXSwiaWF0IjoxNzAwMDAwMDAwLCJleHAiOjE3MDAwMDM2MDB9.signature"
        )
        String accessToken,

        @Schema(
                description = "Refresh token used to obtain a new access token when it expires. Valid for longer period (default 7 days).",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID refreshToken
) { }

