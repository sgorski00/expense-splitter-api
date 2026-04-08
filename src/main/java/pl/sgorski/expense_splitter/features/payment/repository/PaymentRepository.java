package pl.sgorski.expense_splitter.features.payment.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.sgorski.expense_splitter.features.payment.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {}
