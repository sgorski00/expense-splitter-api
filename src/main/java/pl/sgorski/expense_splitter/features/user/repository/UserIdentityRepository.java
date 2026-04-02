package pl.sgorski.expense_splitter.features.user.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.domain.UserIdentity;

public interface UserIdentityRepository extends JpaRepository<UserIdentity, UUID> {
  boolean existsByProviderAndProviderId(AuthProvider authProvider, String providerId);

  @EntityGraph(attributePaths = "user")
  Optional<UserIdentity> findWithUserByProviderAndProviderId(
      AuthProvider provider, String providerId);

  void deleteAllByUser(User user);
}
