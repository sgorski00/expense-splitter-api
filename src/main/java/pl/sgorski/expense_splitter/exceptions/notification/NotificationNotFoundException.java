package pl.sgorski.expense_splitter.exceptions.notification;

import java.util.UUID;
import pl.sgorski.expense_splitter.exceptions.NotFoundException;

public final class NotificationNotFoundException extends NotFoundException {

  public NotificationNotFoundException(UUID id) {
    super("Notification not found with id: " + id.toString());
  }
}
