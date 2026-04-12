package pl.sgorski.expense_splitter.notification.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import pl.sgorski.expense_splitter.features.user.domain.User;

@Entity
@Table(name = "user_notification_preferences")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserNotificationPreference {

  @Id @GeneratedValue @UuidGenerator @EqualsAndHashCode.Include private UUID id;

  @OneToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(nullable = false, unique = true, name = "user_id")
  private User user;

  @Column(nullable = false)
  private Boolean emailNotificationsEnabled = true;

  @Column(nullable = false)
  private Boolean websocketNotificationsEnabled = true;

  @CreationTimestamp private Instant createdAt;

  @UpdateTimestamp private Instant updatedAt;
}
