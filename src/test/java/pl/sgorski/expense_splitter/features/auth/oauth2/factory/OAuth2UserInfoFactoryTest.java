package pl.sgorski.expense_splitter.features.auth.oauth2.factory;

import org.junit.jupiter.api.Test;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;
import pl.sgorski.expense_splitter.features.auth.oauth2.provider.impl.FacebookOAuth2UserInfo;
import pl.sgorski.expense_splitter.features.auth.oauth2.provider.impl.GoogleOAuth2UserInfo;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class OAuth2UserInfoFactoryTest {

  @Test
  void create_shouldCreateUserInfo_whenProviderIsGoogle() {
    var attributes = new HashMap<String, Object>();
    var provider = AuthProvider.GOOGLE;

    var info = OAuth2UserInfoFactory.create(provider, attributes);

    assertInstanceOf(GoogleOAuth2UserInfo.class, info);
  }

  @Test
  void create_shouldCreateUserInfo_whenProviderIsFacebook() {
    var attributes = new HashMap<String, Object>();
    var provider = AuthProvider.FACEBOOK;

    var info = OAuth2UserInfoFactory.create(provider, attributes);

    assertInstanceOf(FacebookOAuth2UserInfo.class, info);
  }
}
