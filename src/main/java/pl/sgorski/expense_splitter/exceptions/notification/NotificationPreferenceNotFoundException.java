package pl.sgorski.expense_splitter.exceptions.notification;

import java.util.UUID;
import pl.sgorski.expense_splitter.exceptions.NotFoundException;

public final class NotificationPreferenceNotFoundException extends NotFoundException {

  public NotificationPreferenceNotFoundException(UUID userId) {
    super("Notification preferences not found for user with id: " + userId.toString());
  }
}
