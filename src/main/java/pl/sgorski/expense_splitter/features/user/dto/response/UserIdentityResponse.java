package pl.sgorski.expense_splitter.features.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;

@Schema(
    name = "User Identity Response",
    description = "Linked OAuth2 user account's identity info.")
public record UserIdentityResponse(
    @Schema(
            description = "Identity unique identifier.",
            example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
    @Schema(
            description = "Identity provider.",
            example = "FACEBOOK",
            allowableValues = {"GOOGLE", "FACEBOOK"})
        AuthProvider provider,
    @Schema(description = "Identity provider unique identifier.", example = "1234567890")
        String providerId) {}
