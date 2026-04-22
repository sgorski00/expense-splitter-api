package pl.sgorski.expense_splitter.features.expense.repository;

import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.sgorski.expense_splitter.features.expense.domain.Expense;
import pl.sgorski.expense_splitter.features.user.domain.User;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

  @Query(
      """
                        SELECT DISTINCT e FROM Expense e
                        LEFT JOIN e.shares s
                        WHERE (e.payer = :user OR s.user = :user)
                    """)
  Page<Expense> findByUser(User user, Pageable pageable);

  @Query(
      """
                        SELECT e FROM Expense e
                        WHERE e.payer = :user
                    """)
  Page<Expense> findByPayer(User user, Pageable pageable);

  @Query(
      """
                        SELECT e FROM Expense e
                        JOIN e.shares s
                        WHERE s.user = :user
                    """)
  Page<Expense> findByParticipant(User user, Pageable pageable);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select e from Expense e where e.id = :id")
  Optional<Expense> findByIdForUpdate(UUID id);

  @Query(
      """
          SELECT COUNT(e) FROM Expense e
          WHERE e.createdAt BETWEEN :from AND :to AND (
                e.payer.id = :userId OR
                :userId in (select es.user.id FROM ExpenseShare es where es.expense = e))
      """)
  long countByUserAndDateRange(
      @Param("userId") UUID userId, @Param("from") Instant from, @Param("to") Instant to);

  @Query(
      """
          SELECT COALESCE(SUM(e.amountTotal), 0) FROM Expense e
          WHERE e.createdAt BETWEEN :from AND :to AND (
                e.payer.id = :userId OR
                :userId in (select es.user.id FROM ExpenseShare es where es.expense = e))
      """)
  BigDecimal sumAmountByUserAndDateRange(
      @Param("userId") UUID userId, @Param("from") Instant from, @Param("to") Instant to);
}
