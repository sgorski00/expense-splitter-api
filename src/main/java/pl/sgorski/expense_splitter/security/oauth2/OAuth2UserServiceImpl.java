package pl.sgorski.expense_splitter.security.oauth2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;
import pl.sgorski.expense_splitter.features.auth.oauth2.dto.OAuth2LoginContext;
import pl.sgorski.expense_splitter.features.auth.oauth2.factory.OAuth2UserInfoFactory;
import pl.sgorski.expense_splitter.features.auth.oauth2.service.impl.OAuth2AccountLinkService;
import pl.sgorski.expense_splitter.features.auth.oauth2.service.impl.OAuth2CommonLoginService;

@Service
@RequiredArgsConstructor
@Slf4j
public final class OAuth2UserServiceImpl extends DefaultOAuth2UserService {

  private final OAuth2CommonLoginService oAuth2CommonLoginService;
  private final OAuth2AccountLinkService oAuth2AccountLinkService;
  private final OAuth2PayloadResolver oAuth2PayloadResolver;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    log.debug("Loading user from OAuth2 provider");
    var oauthUser = loadUserFromProvider(userRequest);
    var providerStr = userRequest.getClientRegistration().getRegistrationId();
    var provider = AuthProvider.fromString(providerStr);
    var payload = oAuth2PayloadResolver.consume();
    var linkMode = payload.map(OAuth2ContextPayload::mode).orElse(OAuth2Mode.LOGIN);
    var userId = payload.map(OAuth2ContextPayload::userId).orElse(null);
    var context =
        new OAuth2LoginContext(
            OAuth2UserInfoFactory.create(provider, oauthUser.getAttributes()),
            linkMode == OAuth2Mode.LINK,
            userId);

    log.debug(
        "Processing user {} from OAuth2 provider: {}", context.userInfo().getEmail(), providerStr);
    if (context.linkMode()) {
      oAuth2AccountLinkService.handle(context);
    } else {
      oAuth2CommonLoginService.handle(context);
    }
    return oauthUser;
  }

  public OAuth2User loadUserFromProvider(OAuth2UserRequest userRequest) {
    return super.loadUser(userRequest);
  }
}
