package pl.sgorski.expense_splitter.security.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pl.sgorski.expense_splitter.exceptions.user.IdentityNotFoundException;
import pl.sgorski.expense_splitter.features.auth.local.utils.TokenResponseEntityCreator;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;
import pl.sgorski.expense_splitter.features.auth.oauth2.factory.OAuth2UserInfoFactory;
import pl.sgorski.expense_splitter.features.user.service.UserIdentityService;

@Component
@RequiredArgsConstructor
@Slf4j
public final class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

  private final UserIdentityService identityService;
  private final TokenResponseEntityCreator tokenResponseEntityCreator;
  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException {
    var oAuth2Token = (OAuth2AuthenticationToken) authentication;
    var provider = AuthProvider.fromString(oAuth2Token.getAuthorizedClientRegistrationId());
    var principal =
        (OAuth2User) Objects.requireNonNull(authentication.getPrincipal(), "Authentication failed");
    var userInfo = OAuth2UserInfoFactory.create(provider, principal.getAttributes());

    try {
      var identity = identityService.findIdentity(userInfo.getProvider(), userInfo.getProviderId());
      var user = identity.getUser();
      var tokenResponse = tokenResponseEntityCreator.generate(user);

      setCookies(response, tokenResponse.getHeaders());
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setStatus(200);
      var loginResponse = Objects.requireNonNull(tokenResponse.getBody());
      response.getWriter().write(objectMapper.writeValueAsString(loginResponse));

      log.info("OAuth2 authentication successful for provider: {}", provider);
    } catch (IdentityNotFoundException ex) {
      log.warn(
          "OAuth2 authentication failed - local user attempted OAuth login. Provider: {}",
          userInfo.getProvider());
      throw new AccessDeniedException("Local users are not allowed to login with OAuth");
    }
  }

  private void setCookies(HttpServletResponse response, HttpHeaders responseHeaders) {
    responseHeaders
        .getValuesAsList(HttpHeaders.SET_COOKIE)
        .forEach(cookie -> response.addHeader(HttpHeaders.SET_COOKIE, cookie));
  }
}
