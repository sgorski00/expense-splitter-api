package pl.sgorski.expense_splitter.features.auth.oauth2.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.exceptions.AccountLinkingException;
import pl.sgorski.expense_splitter.features.auth.mapper.AuthMapper;
import pl.sgorski.expense_splitter.features.auth.oauth2.dto.OAuth2LoginContext;
import pl.sgorski.expense_splitter.features.auth.oauth2.service.OAuth2LoginService;
import pl.sgorski.expense_splitter.features.user.service.UserIdentityService;
import pl.sgorski.expense_splitter.features.user.service.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2AccountLinkService implements OAuth2LoginService {

    private final AuthMapper authMapper;
    private final UserService userService;
    private final UserIdentityService userIdentityService;

    public OAuth2User handle(OAuth2LoginContext context) {
        var userInfo = context.userInfo();
        var userId = context.linkUserId();

        log.debug("Entering OAuth2 account link mode");
        if (userIdentityService.isUserIdentityPresent(userInfo.getProviderId(), userInfo.getProvider())) {
            log.debug("Someone is using account: {} [{}] already.", userInfo.getEmail(), userInfo.getProvider().name());
            throw new AccountLinkingException("Account is already linked to another user");
        }
        if (userId == null) {
            log.error("There is no user id in the session! Cannot link an oauth2 account");
            throw new AccountLinkingException("User id is required to link an account");
        }
        var user = userService.getUserWithIdentities(userId);
        log.debug("Linking new identity {} to existing user {}", userInfo.getProvider().name(), user.getEmail());
        var identity = authMapper.toIdentity(userInfo);
        user.addIdentity(identity);
        userService.save(user);
        return context.oauthUser();
    }
}
