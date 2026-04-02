package pl.sgorski.expense_splitter.features.auth.oauth2.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import org.junit.jupiter.api.Test;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;
import pl.sgorski.expense_splitter.features.auth.oauth2.provider.impl.FacebookOAuth2UserInfo;

public class FacebookOAuth2UserInfoTest {

  @Test
  void constructor_shouldInitializeFields_whenValidData() {
    var attributes = new HashMap<String, Object>();
    attributes.put("id", "123456789");
    attributes.put("email", "user@example.com");
    attributes.put("first_name", "John");
    attributes.put("last_name", "Doe");

    var userInfo = new FacebookOAuth2UserInfo(attributes);

    assertEquals("123456789", userInfo.getProviderId());
    assertEquals("user@example.com", userInfo.getEmail());
    assertEquals("John", userInfo.getFirstName());
    assertEquals("Doe", userInfo.getLastName());
    assertEquals(AuthProvider.FACEBOOK, userInfo.getProvider());
  }

  @Test
  void constructor_shouldAllowNullFirstAndLastName() {
    var attributes = new HashMap<String, Object>();
    attributes.put("id", "123456789");
    attributes.put("email", "user@example.com");

    var userInfo = new FacebookOAuth2UserInfo(attributes);

    assertNull(userInfo.getFirstName());
    assertNull(userInfo.getLastName());
  }

  @Test
  void constructor_shouldThrowException_whenIdIsNull() {
    var attributes = new HashMap<String, Object>();
    attributes.put("email", "user@example.com");

    assertThrows(IllegalArgumentException.class, () -> new FacebookOAuth2UserInfo(attributes));
  }

  @Test
  void constructor_shouldThrowException_whenIdIsBlank() {
    var attributes = new HashMap<String, Object>();
    attributes.put("id", "   ");
    attributes.put("email", "user@example.com");

    assertThrows(IllegalArgumentException.class, () -> new FacebookOAuth2UserInfo(attributes));
  }

  @Test
  void constructor_shouldThrowException_whenEmailIsNull() {
    var attributes = new HashMap<String, Object>();
    attributes.put("id", "123456789");

    assertThrows(IllegalArgumentException.class, () -> new FacebookOAuth2UserInfo(attributes));
  }

  @Test
  void constructor_shouldThrowException_whenEmailIsBlank() {
    var attributes = new HashMap<String, Object>();
    attributes.put("id", "123456789");
    attributes.put("email", "   ");

    assertThrows(IllegalArgumentException.class, () -> new FacebookOAuth2UserInfo(attributes));
  }
}
