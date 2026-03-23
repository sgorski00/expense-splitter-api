package pl.sgorski.expense_splitter.features.auth.refresh_token.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.sgorski.expense_splitter.features.auth.refresh_token.domain.RefreshToken;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(UUID token);
    void deleteAllByExpiresAtBeforeOrIsRevokedTrue(Instant now);
}
