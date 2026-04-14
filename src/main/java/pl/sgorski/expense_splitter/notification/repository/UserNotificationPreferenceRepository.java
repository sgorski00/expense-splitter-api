package pl.sgorski.expense_splitter.notification.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.notification.domain.UserNotificationPreference;

public interface UserNotificationPreferenceRepository
    extends JpaRepository<UserNotificationPreference, UUID> {

  Optional<UserNotificationPreference> findByUser(User user);
}
