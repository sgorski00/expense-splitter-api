package pl.sgorski.expense_splitter.features.friendship.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.sgorski.expense_splitter.features.friendship.domain.Friendship;
import pl.sgorski.expense_splitter.features.friendship.domain.FriendshipStatus;
import pl.sgorski.expense_splitter.features.user.domain.User;

import java.util.UUID;

public interface FriendshipRepository extends JpaRepository<Friendship, UUID> {

    @Query("SELECT f FROM Friendship f WHERE (f.requester = :user OR f.recipient = :user) AND f.status = :status AND f.deletedAt IS NULL")
    Page<Friendship> findFriendshipByUserAndStatus(User user, FriendshipStatus status, Pageable pageable);

    @Query("""
            SELECT f FROM Friendship f JOIN FETCH f.requester join fetch f.recipient
            WHERE (f.requester = :user OR f.recipient = :user) AND f.status = 'ACCEPTED' AND f.deletedAt IS NULL
            """)
    Page<Friendship> findFriends(User user, Pageable pageable);
}
