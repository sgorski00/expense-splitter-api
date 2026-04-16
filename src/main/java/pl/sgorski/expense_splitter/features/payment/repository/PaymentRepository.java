package pl.sgorski.expense_splitter.features.payment.repository;

import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.sgorski.expense_splitter.features.expense.domain.Expense;
import pl.sgorski.expense_splitter.features.payment.domain.Payment;
import pl.sgorski.expense_splitter.features.user.domain.User;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
  @EntityGraph(attributePaths = {"expense", "payer"})
  Page<Payment> findByPayer(User user, Pageable pageable);

  @EntityGraph(attributePaths = {"expense", "payer"})
  Page<Payment> findByExpense(Expense expense, Pageable pageable);

  @Query(
      "select coalesce(sum(p.amount), 0) from Payment p where p.expense = :expense and p.payer.id = :userId")
  BigDecimal sumByExpenseAndUserId(Expense expense, UUID userId);
}
