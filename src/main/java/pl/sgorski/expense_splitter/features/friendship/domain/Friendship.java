package pl.sgorski.expense_splitter.features.friendship.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.jspecify.annotations.Nullable;
import pl.sgorski.expense_splitter.exceptions.FriendshipStatusChangeException;
import pl.sgorski.expense_splitter.features.user.domain.User;

@Entity
@Table(name = "friendships")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"requester", "recipient"})
@NoArgsConstructor
@SQLDelete(sql = "UPDATE friendships SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class Friendship {

  @Id @GeneratedValue @UuidGenerator @EqualsAndHashCode.Include private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "requester_id", nullable = false)
  private User requester;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "recipient_id", nullable = false)
  private User recipient;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private FriendshipStatus status = FriendshipStatus.PENDING;

  @CreationTimestamp private Instant createdAt;

  @UpdateTimestamp private Instant updatedAt;

  @Nullable private Instant deletedAt;

  public void changeStatus(FriendshipStatus status) {
    if (this.status == FriendshipStatus.ACCEPTED || this.status == FriendshipStatus.REJECTED) {
      throw new FriendshipStatusChangeException();
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
