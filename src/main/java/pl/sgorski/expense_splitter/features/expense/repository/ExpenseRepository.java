package pl.sgorski.expense_splitter.features.expense.repository;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}
