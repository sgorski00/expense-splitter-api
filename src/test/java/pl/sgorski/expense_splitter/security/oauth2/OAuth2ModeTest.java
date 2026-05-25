package pl.sgorski.expense_splitter.security.oauth2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

public class OAuth2ModeTest {

  @Test
  void fromString_shouldReturnLOGIN_whenValueIsLoginUppercase() {
    assertEquals(OAuth2Mode.LOGIN, OAuth2Mode.fromString("LOGIN"));
  }

  @Test
  void fromString_shouldReturnLOGIN_whenValueIsLoginLowercase() {
    assertEquals(OAuth2Mode.LOGIN, OAuth2Mode.fromString("login"));
  }

  @Test
  void fromString_shouldReturnLINK_whenValueIsLinkUppercase() {
    assertEquals(OAuth2Mode.LINK, OAuth2Mode.fromString("LINK"));
  }

  @Test
  void fromString_shouldReturnLINK_whenValueIsLinkWithSpaces() {
    assertEquals(OAuth2Mode.LINK, OAuth2Mode.fromString("  link  "));
  }

  @Test
  void fromString_shouldThrowException_whenValueIsUnknown() {
    assertThrows(NoSuchElementException.class, () -> OAuth2Mode.fromString("unknown"));
  }

  @Test
  void fromString_shouldThrowException_whenValueIsNull() {
    assertThrows(NullPointerException.class, () -> OAuth2Mode.fromString(null));
  }
}
