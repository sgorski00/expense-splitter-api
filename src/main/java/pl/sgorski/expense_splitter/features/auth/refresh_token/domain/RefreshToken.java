package pl.sgorski.expense_splitter.features.auth.refresh_token.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;
import pl.sgorski.expense_splitter.exceptions.authentication.RefreshTokenValidationException;
import pl.sgorski.expense_splitter.features.user.domain.User;

@Entity
@Table(name = "refresh_tokens")
@Data
@ToString(exclude = "user")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class RefreshToken {

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

  public RefreshToken(UUID token, User user, Long expirationTimeInMs) {
    this.token = token;
    this.user = user;
    this.expiresAt = Instant.now().plusMillis(expirationTimeInMs);
  }

  public void validate() {
    if (this.isRevoked) {
      throw new RefreshTokenValidationException("Refresh token is revoked");
    }
    if (this.expiresAt.isBefore(Instant.now())) {
      throw new RefreshTokenValidationException("Refresh token is expired");
    }
  }
}
