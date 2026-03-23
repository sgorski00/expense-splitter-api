package pl.sgorski.expense_splitter.features.auth.refresh_token.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;
import pl.sgorski.expense_splitter.features.user.domain.User;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Data
@ToString(exclude = "user")
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

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
}
