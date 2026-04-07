package pl.sgorski.expense_splitter.features.expense.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.sgorski.expense_splitter.features.expense.domain.ExpenseShare;

public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, UUID> {}
