package pl.sgorski.expense_splitter.features.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import pl.sgorski.expense_splitter.config.CentralMapperConfig;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.domain.UserIdentity;
import pl.sgorski.expense_splitter.features.user.dto.command.CreateUserCommand;
import pl.sgorski.expense_splitter.features.user.dto.command.UpdateUserCommand;
import pl.sgorski.expense_splitter.features.user.dto.request.CreateUserRequest;
import pl.sgorski.expense_splitter.features.user.dto.request.UpdateProfileRequest;
import pl.sgorski.expense_splitter.features.user.dto.request.UpdateUserRequest;
import pl.sgorski.expense_splitter.features.user.dto.response.DetailedUserResponse;
import pl.sgorski.expense_splitter.features.user.dto.response.SimpleUserResponse;
import pl.sgorski.expense_splitter.features.user.dto.response.UserIdentityResponse;
import pl.sgorski.expense_splitter.features.user.dto.response.UserResponse;
import pl.sgorski.expense_splitter.utils.StringUtils;

@Mapper(config = CentralMapperConfig.class)
public interface UserMapper {
  @Mapping(target = "email", source = "email", qualifiedByName = "encryptedEmail")
  SimpleUserResponse toSimpleResponse(User user);

  UserResponse toResponse(User user);

  DetailedUserResponse toDetailedResponse(User user);

  UserIdentityResponse toIdentityResponse(UserIdentity identity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "role", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "passwordHash", ignore = true)
  @Mapping(target = "passwordForChange", ignore = true)
  @Mapping(target = "identities", ignore = true)
  @Mapping(target = "authorities", ignore = true)
  @Mapping(target = "sentFriendshipRequests", ignore = true)
  @Mapping(target = "receivedFriendshipRequests", ignore = true)
  @Mapping(target = "notificationPreference", ignore = true)
  void updateProfile(UpdateProfileRequest request, @MappingTarget User user);

  @Mapping(target = "password", source = "newPassword")
  CreateUserCommand toCreateCommand(CreateUserRequest request);

  UpdateUserCommand toUpdateCommand(UpdateUserRequest request);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "passwordHash", ignore = true)
  @Mapping(target = "passwordForChange", ignore = true)
  @Mapping(target = "identities", ignore = true)
  @Mapping(target = "authorities", ignore = true)
  @Mapping(target = "sentFriendshipRequests", ignore = true)
  @Mapping(target = "receivedFriendshipRequests", ignore = true)
  @Mapping(target = "notificationPreference", ignore = true)
  void updateUser(UpdateUserCommand command, @MappingTarget User user);

  @Named("encryptedEmail")
  static String encryptedEmail(String email) {
    var visibleChars = 3;
    return StringUtils.encryptEmail(email, visibleChars);
  }

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "passwordHash", ignore = true)
  @Mapping(target = "passwordForChange", ignore = true)
  @Mapping(target = "identities", ignore = true)
  @Mapping(target = "authorities", ignore = true)
  @Mapping(target = "sentFriendshipRequests", ignore = true)
  @Mapping(target = "receivedFriendshipRequests", ignore = true)
  @Mapping(target = "notificationPreference", ignore = true)
  User toUser(CreateUserCommand command);
}
