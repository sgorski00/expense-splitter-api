package pl.sgorski.expense_splitter.features.payment.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import pl.sgorski.expense_splitter.features.expense.domain.Expense;
import pl.sgorski.expense_splitter.features.user.domain.User;

@Table(name = "payments")
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Data
public class Payment {

  @Id @GeneratedValue @UuidGenerator @EqualsAndHashCode.Include private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "payer_id", nullable = false)
  private User payer;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "expense_id", nullable = false)
  private Expense expense;

  @Column(nullable = false)
  private BigDecimal amount;

  @CreationTimestamp private Instant createdAt;

  @UpdateTimestamp private Instant updatedAt;

  public boolean isParticipant(UUID userId) {
    return payer.getId().equals(userId) || expense.isParticipant(userId);
  }
}
