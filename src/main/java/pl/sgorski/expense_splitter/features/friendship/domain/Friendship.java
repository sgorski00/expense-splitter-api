package pl.sgorski.expense_splitter.features.friendship.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.jspecify.annotations.Nullable;
import pl.sgorski.expense_splitter.features.user.domain.User;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "friendships")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@SQLDelete(sql = "UPDATE friendships SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class Friendship {

    @Id
    @UuidGenerator
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FriendshipStatus status = FriendshipStatus.PENDING;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @Nullable
    private Instant deletedAt;

    public void changeStatus(FriendshipStatus status) {
        if(this.status == FriendshipStatus.ACCEPTED || this.status == FriendshipStatus.REJECTED) {
            throw new IllegalStateException("Cannot change status of this friendship");
        }
        setStatus(status);
    }

    private void setStatus(FriendshipStatus status) {
        this.status = status;
    }

    public void delete() {
        this.deletedAt = Instant.now();
    }
}
