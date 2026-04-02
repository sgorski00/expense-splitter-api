package pl.sgorski.expense_splitter.features.auth.refresh_token.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.exceptions.not_found.NotFoundException;
import pl.sgorski.expense_splitter.exceptions.not_found.RefreshTokenNotFoundException;
import pl.sgorski.expense_splitter.features.auth.refresh_token.config.RefreshTokenProperties;
import pl.sgorski.expense_splitter.features.auth.refresh_token.domain.RefreshToken;
import pl.sgorski.expense_splitter.features.auth.refresh_token.repository.RefreshTokenRepository;
import pl.sgorski.expense_splitter.features.user.domain.User;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenProperties refreshTokenProperties;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken generateRefreshToken(User user) {
        var tokenUUID = UUID.randomUUID();
        var token = new RefreshToken(tokenUUID, user, refreshTokenProperties.expirationTimeInMs());
        return refreshTokenRepository.save(token);
    }

    @Transactional
    public void revokeToken(UUID tokenValue) {
        var token = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(RefreshTokenNotFoundException::new);
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    /**
     * Useful when password is changed to force re-authentication.
     */
    @Transactional
    public void revokeAllUserTokens(UUID userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
    }

    public long getExpirationSecond() {
        return refreshTokenProperties.expirationTimeInMs() / 1000;
    }

    @Transactional
    public void deletedInvalidTokens() {
        refreshTokenRepository.deleteAllByExpiresAtBeforeOrIsRevokedTrue(Instant.now());
    }

    public RefreshToken getToken(UUID refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(RefreshTokenNotFoundException::new);
    }
}
