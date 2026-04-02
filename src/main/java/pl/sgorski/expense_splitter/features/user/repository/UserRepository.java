package pl.sgorski.expense_splitter.features.user.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.sgorski.expense_splitter.features.user.domain.Role;
import pl.sgorski.expense_splitter.features.user.domain.User;

public interface UserRepository extends JpaRepository<User, UUID> {
  boolean existsByEmailAndDeletedAtIsNull(String email);

  Optional<User> findByEmailAndDeletedAtIsNull(String email);

  @EntityGraph(attributePaths = "identities")
  Optional<User> findWithIdentitiesByIdAndDeletedAtIsNull(UUID id);

  Optional<User> findByIdAndDeletedAtIsNull(UUID id);

  Long countByRole(Role role);

  @Query(
"""
    SELECT u from User u WHERE u.deletedAt IS NULL AND (
      LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))
      OR LOWER(CONCAT(COALESCE(u.firstName, ''),' ',COALESCE(u.lastName, ''))) LIKE LOWER(CONCAT('%', :query, '%'))
    )
""")
  Page<User> findByQuery(String query, Pageable pageable);
}
