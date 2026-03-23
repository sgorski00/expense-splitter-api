package pl.sgorski.expense_splitter.features.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.exceptions.IdentityNotFoundException;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;
import pl.sgorski.expense_splitter.features.user.domain.UserIdentity;
import pl.sgorski.expense_splitter.features.user.repository.UserIdentityRepository;

@Service
@RequiredArgsConstructor
public final class UserIdentityService {

    private final UserIdentityRepository userIdentityRepository;

    public boolean isUserIdentityPresent(String providerId, AuthProvider authProvider) {
        return userIdentityRepository.existsByProviderAndProviderId(authProvider, providerId);
    }

    public UserIdentity findIdentity(AuthProvider provider, String providerId) {
        return userIdentityRepository.findWithUserByProviderAndProviderId(provider, providerId)
                .orElseThrow(() -> new IdentityNotFoundException("User identity not found for provider: " + provider.name() + ", providerId: " + providerId));
    }
}
