package pl.sgorski.expense_splitter.notification.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.sgorski.expense_splitter.notification.domain.Notification;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {}
