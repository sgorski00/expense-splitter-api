package pl.sgorski.expense_splitter.features.friendship.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.sgorski.expense_splitter.features.friendship.domain.Friendship;
import pl.sgorski.expense_splitter.features.friendship.domain.FriendshipStatus;
import pl.sgorski.expense_splitter.features.user.domain.User;

public interface FriendshipRepository extends JpaRepository<Friendship, UUID> {

  Optional<Friendship> findByIdAndDeletedAtIsNull(UUID uuid);

  @Query(
      "SELECT f FROM Friendship f WHERE (f.requester = :user OR f.recipient = :user) AND f.status = :status AND f.deletedAt IS NULL")
  Page<Friendship> findFriendshipByUserAndStatus(
      User user, FriendshipStatus status, Pageable pageable);

  @EntityGraph(attributePaths = {"requester", "recipient"})
  @Query(
      """
            SELECT f FROM Friendship f
            WHERE (f.requester = :user OR f.recipient = :user) AND f.status = 'ACCEPTED' AND f.deletedAt IS NULL
            """)
  Page<Friendship> findFriends(User user, Pageable pageable);
}
