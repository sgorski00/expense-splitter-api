package pl.sgorski.expense_splitter.features.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.sgorski.expense_splitter.config.CentralMapperConfig;
import pl.sgorski.expense_splitter.features.auth.dto.command.LoginUserCommand;
import pl.sgorski.expense_splitter.features.auth.dto.command.RegisterUserCommand;
import pl.sgorski.expense_splitter.features.auth.dto.request.LoginRequest;
import pl.sgorski.expense_splitter.features.auth.dto.request.RegisterRequest;
import pl.sgorski.expense_splitter.features.auth.oauth2.provider.OAuth2UserInfo;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.domain.UserIdentity;

@Mapper(config = CentralMapperConfig.class)
public interface AuthMapper {

  RegisterUserCommand toCommand(RegisterRequest request);

  LoginUserCommand toCommand(LoginRequest request);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "passwordHash", ignore = true)
  @Mapping(target = "role", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "passwordForChange", ignore = true)
  @Mapping(target = "identities", ignore = true)
  @Mapping(target = "authorities", ignore = true)
  @Mapping(target = "sentFriendshipRequests", ignore = true)
  @Mapping(target = "receivedFriendshipRequests", ignore = true)
  User toEntity(RegisterUserCommand command);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  UserIdentity toIdentity(OAuth2UserInfo userInfo);
}
