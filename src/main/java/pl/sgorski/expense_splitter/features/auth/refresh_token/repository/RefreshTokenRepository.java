package pl.sgorski.expense_splitter.features.auth.refresh_token.repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pl.sgorski.expense_splitter.features.auth.refresh_token.domain.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
  Optional<RefreshToken> findByToken(UUID token);

  void deleteAllByExpiresAtBeforeOrIsRevokedTrue(Instant now);

  @Transactional
  @Modifying
  @Query(
      "update RefreshToken t set t.isRevoked = true where t.user.id = :userId and t.isRevoked = false")
  void revokeAllByUserId(@Param("userId") UUID userId);
}
