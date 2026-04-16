package pl.sgorski.expense_splitter.features.auth.password_reset_token.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;
import pl.sgorski.expense_splitter.exceptions.authentication.PasswordResetTokenValidationException;
import pl.sgorski.expense_splitter.features.user.domain.User;

@Entity
@Table(name = "password_reset_tokens")
@Data
@ToString(exclude = "user")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class PasswordResetToken {

  @Id @GeneratedValue @UuidGenerator @EqualsAndHashCode.Include private UUID id;

  @Column(nullable = false, unique = true)
  private UUID token;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private boolean isRevoked = false;

  @Column(nullable = false)
  private Instant expiresAt;

  @CreationTimestamp private Instant createdAt;

  public PasswordResetToken(UUID token, User user, Long expirationTimeInMs) {
    this.token = token;
    this.user = user;
    this.expiresAt = Instant.now().plusMillis(expirationTimeInMs);
  }

  public void validate() {
    if (this.isRevoked) {
      throw new PasswordResetTokenValidationException("Refresh token is revoked");
    }
    if (this.expiresAt.isBefore(Instant.now())) {
      throw new PasswordResetTokenValidationException("Refresh token is expired");
    }
  }
}
