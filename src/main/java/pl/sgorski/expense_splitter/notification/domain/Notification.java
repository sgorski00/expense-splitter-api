package pl.sgorski.expense_splitter.notification.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;
import pl.sgorski.expense_splitter.features.user.domain.User;

@Entity
@Table(name = "notifications")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Notification {

  @Id @GeneratedValue @UuidGenerator @EqualsAndHashCode.Include private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false, name = "user_id")
  private User user;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String body;

  @Column(nullable = false, name = "read")
  private Boolean isRead = false;

  @CreationTimestamp private Instant createdAt;
}
