package pl.sgorski.expense_splitter.features.expense.repository;

import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.sgorski.expense_splitter.features.expense.domain.Expense;
import pl.sgorski.expense_splitter.features.expense.dto.filter.ExpenseRole;
import pl.sgorski.expense_splitter.features.user.domain.User;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
  @Query(
      """
        SELECT e FROM Expense e
        LEFT JOIN e.shares s
        WHERE (e.payer = :user AND (:role IS NULL OR :role = 'PAYER'))
           OR (s.user = :user AND (:role IS NULL OR :role = 'PARTICIPANT'))
        GROUP BY e.id
    """)
  Page<Expense> findByUserAndRole(User user, @Nullable ExpenseRole role, Pageable pageable);
}
