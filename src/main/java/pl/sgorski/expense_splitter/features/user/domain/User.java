package pl.sgorski.expense_splitter.features.user.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.*;

@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(columnNames = {"email", "deleted_at"})
)
@Data
@ToString(exclude = {"passwordHash", "identities"})
@EqualsAndHashCode(exclude = "identities")
@NoArgsConstructor
@SQLDelete(sql = "UPDATE users SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class User implements UserDetails {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String email;

    @Nullable
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Nullable
    private String firstName;

    @Nullable
    private String lastName;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UserIdentity> identities = new HashSet<>();

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @Nullable
    private Instant deletedAt;

    @Column(nullable = false)
    private boolean isPasswordForChange = false;

    public void addIdentity(UserIdentity identity) {
        identities.stream()
                .filter(i -> i.getProvider().equals(identity.getProvider()))
                .findFirst()
                .ifPresentOrElse(
                        i -> { throw new IllegalStateException("User already has identity for provider: " + i.getProvider()); },
                        () -> {
                            identities.add(identity);
                            identity.setUser(this);
                        });
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role);
    }

    @Override
    public @Nullable String getPassword() {
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.deletedAt == null;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.deletedAt == null;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.deletedAt == null;
    }

    public void delete() {
        this.deletedAt = Instant.now();
    }
}
