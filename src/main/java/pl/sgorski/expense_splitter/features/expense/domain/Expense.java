package pl.sgorski.expense_splitter.features.expense.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.jspecify.annotations.Nullable;
import pl.sgorski.expense_splitter.features.user.domain.User;

@Entity
@Table(name = "expenses")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "shares")
@NoArgsConstructor
@Data
public class Expense {

  @Id @GeneratedValue @UuidGenerator @EqualsAndHashCode.Include private UUID id;

  @Column(nullable = false)
  private String title;

  @Nullable private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "payer_id", nullable = false)
  private User payer;

  @Column(nullable = false)
  private BigDecimal amountTotal;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SplitType splitType = SplitType.EQUAL;

  @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<ExpenseShare> shares = new HashSet<>();

  @Column(nullable = false)
  private Instant expenseDate;

  @CreationTimestamp private Instant createdAt;

  @UpdateTimestamp private Instant updatedAt;

  public void addShare(ExpenseShare share) {
    shares.add(share);
    share.setExpense(this);
  }

  private void setShares(Set<ExpenseShare> shares) {}

  public boolean isParticipant(User user) {
    return this.getPayer().equals(user)
        || shares.stream().anyMatch(share -> share.getUser().equals(user));
  }
}
