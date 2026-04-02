package pl.sgorski.expense_splitter.features.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.sgorski.expense_splitter.config.CentralMapperConfig;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.domain.UserIdentity;
import pl.sgorski.expense_splitter.features.user.dto.request.UpdateUserRequest;
import pl.sgorski.expense_splitter.features.user.dto.response.DetailedUserResponse;
import pl.sgorski.expense_splitter.features.user.dto.response.SimpleUserResponse;
import pl.sgorski.expense_splitter.features.user.dto.response.UserIdentityResponse;
import pl.sgorski.expense_splitter.features.user.dto.response.UserResponse;

@Mapper(config = CentralMapperConfig.class)
public interface UserMapper {
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
  void updateUser(UpdateUserRequest request, @MappingTarget User user);
}
