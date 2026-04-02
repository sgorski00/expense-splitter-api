package pl.sgorski.expense_splitter.features.auth.oauth2.factory;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import org.junit.jupiter.api.Test;
import pl.sgorski.expense_splitter.exceptions.OAuth2InvalidAttributesException;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;
import pl.sgorski.expense_splitter.features.auth.oauth2.provider.impl.FacebookOAuth2UserInfo;
import pl.sgorski.expense_splitter.features.auth.oauth2.provider.impl.GoogleOAuth2UserInfo;

public class OAuth2UserInfoFactoryTest {

  @Test
  void create_shouldCreateUserInfo_whenProviderIsGoogle() {
    var attributes = new HashMap<String, Object>();
    attributes.put("sub", "123");
    attributes.put("email", "user@example.com");
    var provider = AuthProvider.GOOGLE;

    var info = OAuth2UserInfoFactory.create(provider, attributes);

    assertInstanceOf(GoogleOAuth2UserInfo.class, info);
  }

  @Test
  void create_shouldCreateUserInfo_whenProviderIsFacebook() {
    var attributes = new HashMap<String, Object>();
    attributes.put("id", "123");
    attributes.put("email", "user@example.com");
    var provider = AuthProvider.FACEBOOK;

    var info = OAuth2UserInfoFactory.create(provider, attributes);

    assertInstanceOf(FacebookOAuth2UserInfo.class, info);
  }

  @Test
  void create_shouldThrowOAuth2WrongAttributesException_whenAttributesAreMissing() {
    var attributes = new HashMap<String, Object>();
    var provider = AuthProvider.FACEBOOK;

    assertThrows(
        OAuth2InvalidAttributesException.class,
        () -> OAuth2UserInfoFactory.create(provider, attributes));
  }
}
