package pl.sgorski.expense_splitter.notification.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.sgorski.expense_splitter.config.CentralMapperConfig;
import pl.sgorski.expense_splitter.notification.domain.Notification;
import pl.sgorski.expense_splitter.notification.domain.UserNotificationPreference;
import pl.sgorski.expense_splitter.notification.dto.command.UpdateNotificationRequestCommand;
import pl.sgorski.expense_splitter.notification.dto.request.UpdateNotificationPreferenceRequest;
import pl.sgorski.expense_splitter.notification.dto.response.NotificationPreferenceResponse;
import pl.sgorski.expense_splitter.notification.dto.response.NotificationResponse;

@Mapper(config = CentralMapperConfig.class)
public interface NotificationMapper {

  @Mapping(target = "userId", source = "user.id")
  NotificationResponse toResponse(Notification notification);

  @Mapping(target = "userId", source = "user.id")
  NotificationPreferenceResponse toPreferenceResponse(UserNotificationPreference preference);

  UpdateNotificationRequestCommand toUpdateCommand(
      UpdateNotificationPreferenceRequest notification);
}
