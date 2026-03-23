package pl.sgorski.expense_splitter.features.auth.refresh_token.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.exceptions.NotFoundException;
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

    /**
     * Validates the refresh token, throws exception if invalid.
     *
     * @param tokenValue the token value
     * @param user     the user
     * @throws NotFoundException if token not found or invalid
     */
    public void validateToken(UUID tokenValue, User user) {
        if (!isTokenValid(tokenValue, user)) {
            throw new NotFoundException("Invalid or expired refresh token");
        }
    }

    @Transactional
    public void revokeToken(UUID tokenValue) {
        var token = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new NotFoundException("Token not found"));
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    /**
     * Revokes all refresh tokens for a given user.
     * Useful when password is changed to force re-authentication.
     *
     * @param userId the user ID
     */
    @Transactional
    public void revokeAllUserTokens(UUID userId) {
        var tokens = refreshTokenRepository.findAll().stream()
                .filter(token -> token.getUser().getId().equals(userId) && !token.isRevoked())
                .toList();
        tokens.forEach(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

    public long getExpirationSecond() {
        return refreshTokenProperties.expirationTimeInMs() / 1000;
    }


    private boolean isTokenValid(UUID tokenValue, User user) {
        var token = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new NotFoundException("Token not found"));
        return !token.isRevoked() &&
                token.getExpiresAt().isAfter(Instant.now()) &&
                token.getUser().getId().equals(user.getId());
    }

    @Transactional
    public void deletedInvalidTokens() {
        refreshTokenRepository.deleteAllByExpiresAtBeforeOrIsRevokedTrue(Instant.now());
    }
}
