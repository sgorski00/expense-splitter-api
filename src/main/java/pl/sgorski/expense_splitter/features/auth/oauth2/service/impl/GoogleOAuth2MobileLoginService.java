package pl.sgorski.expense_splitter.features.auth.oauth2.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;
import pl.sgorski.expense_splitter.features.auth.oauth2.config.GoogleOAuth2Properties;
import pl.sgorski.expense_splitter.features.auth.oauth2.dto.OAuth2LoginContext;
import pl.sgorski.expense_splitter.features.auth.oauth2.factory.OAuth2UserInfoFactory;
import pl.sgorski.expense_splitter.features.auth.oauth2.service.OAuth2MobileLoginService;
import pl.sgorski.expense_splitter.features.user.domain.User;

@Service
@RequiredArgsConstructor
public class GoogleOAuth2MobileLoginService implements OAuth2MobileLoginService {

  private final JwtDecoder jwtDecoder;
  private final GoogleOAuth2Properties googleOAuth2Properties;
  private final OAuth2CommonLoginService oAuth2CommonLoginService;

  @Override
  public User handle(String idToken) {
    var jwt = jwtDecoder.decode(idToken);
    validate(jwt);
    var attributes = jwt.getClaims();
    var userInfo = OAuth2UserInfoFactory.create(AuthProvider.GOOGLE, attributes);
    var context = new OAuth2LoginContext(userInfo, false, null);
    return oAuth2CommonLoginService.handle(context);
  }

  private void validate(Jwt jwt) {
    if (!jwt.getAudience().contains(googleOAuth2Properties.clientId())) {
      throw new OAuth2AuthenticationException("Invalid token's audience");
    }

    var issuer = jwt.getIssuer();
    if (issuer == null
        || !(issuer.toString().equals("https://accounts.google.com")
            || issuer.toString().equals("accounts.google.com"))) {
      throw new OAuth2AuthenticationException("Invalid token's issuer");
    }
  }
}
