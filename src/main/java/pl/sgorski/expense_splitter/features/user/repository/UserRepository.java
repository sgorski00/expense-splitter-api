package pl.sgorski.expense_splitter.features.user.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.sgorski.expense_splitter.features.user.domain.Role;
import pl.sgorski.expense_splitter.features.user.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmailAndDeletedAtIsNull(String email);
    Optional<User> findByEmailAndDeletedAtIsNull(String email);
    @EntityGraph(attributePaths = "identities") Optional<User> findWithIdentitiesByIdAndDeletedAtIsNull(UUID id);
    Optional<User> findByIdAndDeletedAtIsNull(UUID id);

    List<User> Role(Role role);

    Long countByRole(Role role);
}
