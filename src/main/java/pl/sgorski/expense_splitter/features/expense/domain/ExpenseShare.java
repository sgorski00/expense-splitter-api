package pl.sgorski.expense_splitter.features.expense.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import pl.sgorski.expense_splitter.features.user.domain.User;

@Entity
@Table(name = "expense_shares")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Data
public class ExpenseShare {

  @Id @GeneratedValue @UuidGenerator @EqualsAndHashCode.Include private UUID id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "expense_id", nullable = false)
  private Expense expense;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private BigDecimal amount;
}
