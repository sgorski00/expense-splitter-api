package pl.sgorski.expense_splitter.features.auth.oauth2.provider;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import org.junit.jupiter.api.Test;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;
import pl.sgorski.expense_splitter.features.auth.oauth2.provider.impl.GoogleOAuth2UserInfo;

public class GoogleOAuth2UserInfoTest {

  @Test
  void constructor_shouldInitializeFields_whenValidData() {
    var attributes = new HashMap<String, Object>();
    attributes.put("sub", "123456789");
    attributes.put("email", "user@example.com");
    attributes.put("given_name", "John");
    attributes.put("family_name", "Doe");

    var userInfo = new GoogleOAuth2UserInfo(attributes);

    assertEquals("123456789", userInfo.getProviderId());
    assertEquals("user@example.com", userInfo.getEmail());
    assertEquals("John", userInfo.getFirstName());
    assertEquals("Doe", userInfo.getLastName());
    assertEquals(AuthProvider.GOOGLE, userInfo.getProvider());
  }

  @Test
  void constructor_shouldAllowNullFirstAndLastName() {
    var attributes = new HashMap<String, Object>();
    attributes.put("sub", "123456789");
    attributes.put("email", "user@example.com");

    var userInfo = new GoogleOAuth2UserInfo(attributes);

    assertNull(userInfo.getFirstName());
    assertNull(userInfo.getLastName());
  }

  @Test
  void constructor_shouldThrowException_whenIdIsNull() {
    var attributes = new HashMap<String, Object>();
    attributes.put("email", "user@example.com");

    assertThrows(IllegalArgumentException.class, () -> new GoogleOAuth2UserInfo(attributes));
  }

  @Test
  void constructor_shouldThrowException_whenIdIsBlank() {
    var attributes = new HashMap<String, Object>();
    attributes.put("sub", "   ");
    attributes.put("email", "user@example.com");

    assertThrows(IllegalArgumentException.class, () -> new GoogleOAuth2UserInfo(attributes));
  }

  @Test
  void constructor_shouldThrowException_whenEmailIsNull() {
    var attributes = new HashMap<String, Object>();
    attributes.put("sub", "123456789");

    assertThrows(IllegalArgumentException.class, () -> new GoogleOAuth2UserInfo(attributes));
  }

  @Test
  void constructor_shouldThrowException_whenEmailIsBlank() {
    var attributes = new HashMap<String, Object>();
    attributes.put("sub", "123456789");
    attributes.put("email", "   ");

    assertThrows(IllegalArgumentException.class, () -> new GoogleOAuth2UserInfo(attributes));
  }
}
