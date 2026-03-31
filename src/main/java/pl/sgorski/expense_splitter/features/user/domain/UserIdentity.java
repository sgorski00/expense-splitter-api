package pl.sgorski.expense_splitter.features.user.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;

import java.util.UUID;

@Entity
@Table(
        name = "user_identities",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"provider", "provider_id"}),
                @UniqueConstraint(columnNames = {"user_id", "provider"})
        }
)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class UserIdentity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @Column(nullable = false)
    private String providerId;
}
