package pl.sgorski.expense_splitter.features.auth.oauth2.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.exceptions.authentication.AccountLinkRequiredException;
import pl.sgorski.expense_splitter.features.auth.mapper.AuthMapper;
import pl.sgorski.expense_splitter.features.auth.oauth2.dto.OAuth2LoginContext;
import pl.sgorski.expense_splitter.features.auth.oauth2.service.OAuth2LoginService;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.service.UserIdentityService;
import pl.sgorski.expense_splitter.features.user.service.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2CommonLoginService implements OAuth2LoginService {

  private final AuthMapper authMapper;
  private final UserService userService;
  private final UserIdentityService userIdentityService;

  public OAuth2User handle(OAuth2LoginContext context) {
    var userInfo = context.userInfo();
    var oauthUser = context.oauthUser();

    log.debug("Entering OAuth2 login/register mode");
    if (userIdentityService.isUserIdentityPresent(
        userInfo.getProviderId(), userInfo.getProvider())) {
      log.debug(
          "Existing oauth user identity detected: {}, {}. Logging in...",
          userInfo.getEmail(),
          userInfo.getProvider().name());
      return oauthUser;
    }

    if (userService.isUserPresent(userInfo.getEmail())) {
      log.warn("Local user with email {} already exists. OAuthLogin blocked", userInfo.getEmail());
      throw new AccountLinkRequiredException();
    }

    var user = new User();
    user.setEmail(userInfo.getEmail());
    user.setFirstName(userInfo.getFirstName());
    user.setLastName(userInfo.getLastName());
    log.debug(
        "New user {} created. Linking identity {}...",
        user.getEmail(),
        userInfo.getProvider().name());
    var identity = authMapper.toIdentity(userInfo);
    user.addIdentity(identity);
    userService.save(user);
    return oauthUser;
  }
}
