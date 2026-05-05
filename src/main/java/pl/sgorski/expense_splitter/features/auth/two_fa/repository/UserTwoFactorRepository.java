package pl.sgorski.expense_splitter.features.auth.two_fa.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.sgorski.expense_splitter.features.auth.two_fa.domain.UserTwoFactor;

public interface UserTwoFactorRepository extends JpaRepository<UserTwoFactor, UUID> {
  Optional<UserTwoFactor> findByUserId(UUID userId);
}
