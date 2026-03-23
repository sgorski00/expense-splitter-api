package pl.sgorski.expense_splitter.security.authenticated;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.service.UserIdentityService;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public final class SecurityAuthenticatedUserResolver implements AuthenticatedUserResolver {

    private final UserIdentityService identityService;

    @Override
    public UUID requireUserId(Authentication authentication) {
        var principal = authentication.getPrincipal();

        if (principal instanceof User user) {
            return user.getId();
        }
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            var providerId = oauthToken.getName();
            var provider = AuthProvider.fromString(oauthToken.getAuthorizedClientRegistrationId());
            var identity = identityService.findIdentity(provider, providerId);
            return identity.getUser().getId();
        }

        throw new IllegalStateException("Unsupported authentication principal");
    }
}
