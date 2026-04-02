package pl.sgorski.expense_splitter.features.auth.oauth2.provider.impl;

import java.util.Map;
import lombok.Getter;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;
import pl.sgorski.expense_splitter.features.auth.oauth2.provider.OAuth2UserInfo;
import pl.sgorski.expense_splitter.utils.StringUtils;

@Getter
public final class GoogleOAuth2UserInfo extends OAuth2UserInfo {

  public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
    this.attributes = attributes;
    this.provider = AuthProvider.GOOGLE;
    this.providerId = StringUtils.requireString((String) attributes.get("sub"));
    this.email = StringUtils.requireString((String) attributes.get("email"));
    this.firstName = (String) attributes.get("given_name");
    this.lastName = (String) attributes.get("family_name");
  }
}
