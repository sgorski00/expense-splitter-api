package pl.sgorski.expense_splitter.features.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sgorski.expense_splitter.exceptions.UserNotFoundException;
import pl.sgorski.expense_splitter.features.auth.refresh_token.service.RefreshTokenService;
import pl.sgorski.expense_splitter.features.user.domain.Role;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final UserIdentityService userIdentityService;

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    public User getUser(String email) {
        return userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    public User getUser(UUID id) {
        return userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User getUserWithIdentities(UUID id) {
        return userRepository.findWithIdentitiesByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public boolean isUserPresent(String email) {
        return userRepository.existsByEmailAndDeletedAtIsNull(email);
    }

    @Transactional
    public void deleteUser(User user) {
        refreshTokenService.revokeAllUserTokens(user.getId());
        userIdentityService.removeAllUserIdentities(user);
        userRepository.delete(user);
    }

    public boolean isAdminPresent() {
        return userRepository.countByRole(Role.ADMIN) > 0;
    }
}
