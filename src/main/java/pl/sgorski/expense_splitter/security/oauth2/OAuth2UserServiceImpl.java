package pl.sgorski.expense_splitter.security.oauth2;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;
import pl.sgorski.expense_splitter.features.auth.oauth2.dto.OAuth2LoginContext;
import pl.sgorski.expense_splitter.features.auth.oauth2.factory.OAuth2UserInfoFactory;
import pl.sgorski.expense_splitter.features.auth.oauth2.service.impl.OAuth2AccountLinkService;
import pl.sgorski.expense_splitter.features.auth.oauth2.service.impl.OAuth2CommonLoginService;
import pl.sgorski.expense_splitter.security.oauth2.session.OAuth2SessionService;

@Service
@RequiredArgsConstructor
@Slf4j
public final class OAuth2UserServiceImpl extends DefaultOAuth2UserService {

    private final OAuth2CommonLoginService oAuth2CommonLoginService;
    private final OAuth2AccountLinkService oAuth2AccountLinkService;
    private final OAuth2SessionService oAuth2SessionService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.debug("Loading user from OAuth2 provider");
        var oauthUser = super.loadUser(userRequest);
        var providerStr = userRequest.getClientRegistration().getRegistrationId();
        var provider = AuthProvider.fromString(providerStr);

        var session = getSession();
        var context = new OAuth2LoginContext(
                oauthUser,
                provider,
                OAuth2UserInfoFactory.create(provider, oauthUser.getAttributes()),
                oAuth2SessionService.isLinkMode(session),
                oAuth2SessionService.getOAuthLinkUserId(session)
        );
        oAuth2SessionService.clearOAuthAttributes(session);

        log.debug("Processing user {} from OAuth2 provider: {}", context.userInfo().getEmail(), providerStr);
        if (context.linkMode()) {
            return oAuth2AccountLinkService.handle(context);
        }
        return oAuth2CommonLoginService.handle(context);
    }

    private HttpSession getSession() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest()
                .getSession(true);
    }
}
