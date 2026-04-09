package pl.sgorski.expense_splitter.features.auth.oauth2.factory;

import java.util.Map;
import pl.sgorski.expense_splitter.exceptions.authentication.OAuth2InvalidAttributesException;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;
import pl.sgorski.expense_splitter.features.auth.oauth2.provider.OAuth2UserInfo;
import pl.sgorski.expense_splitter.features.auth.oauth2.provider.impl.FacebookOAuth2UserInfo;
import pl.sgorski.expense_splitter.features.auth.oauth2.provider.impl.GoogleOAuth2UserInfo;

public final class OAuth2UserInfoFactory {

  public static OAuth2UserInfo create(AuthProvider provider, Map<String, Object> attributes) {
    try {
      return switch (provider) {
        case GOOGLE -> new GoogleOAuth2UserInfo(attributes);
        case FACEBOOK -> new FacebookOAuth2UserInfo(attributes);
      };
    } catch (IllegalArgumentException e) {
      throw new OAuth2InvalidAttributesException(provider, e);
    }
  }
}
