package pl.sgorski.expense_splitter.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import pl.sgorski.expense_splitter.exceptions.InvalidEmailException;

public class StringUtilsTest {

  @Test
  void requireString_shouldReturnString_whenNotBlank() {
    var value = ".";

    var result = StringUtils.requireString(value);

    assertEquals(value, result);
  }

  @Test
  void requireString_shouldThrowIllegalArgumentException_whenValueIsBlank() {
    var value = " ";

    assertThrows(IllegalArgumentException.class, () -> StringUtils.requireString(value));
  }

  @Test
  void requireString_shouldThrowIllegalArgumentException_whenValueIsEmpty() {
    var value = "";

    assertThrows(IllegalArgumentException.class, () -> StringUtils.requireString(value));
  }

  @Test
  void requireString_shouldThrowIllegalArgumentException_whenValueIsNull() {
    assertThrows(IllegalArgumentException.class, () -> StringUtils.requireString(null));
  }

  @Test
  void encryptEmail_shouldEncryptPartOfEmail_whenEmailIsValid() {
    var email = "testuser@example.com";
    var expected = "tes*****@example.com";

    var result = StringUtils.encryptEmail(email, 3);

    assertEquals(expected, result);
  }

  @Test
  void encryptEmail_shouldEncryptWholeEmail_whenEmailLengthIsEqualToVisiblePart() {
    var email = "user@example.com";
    var expected = "****@example.com";

    var result = StringUtils.encryptEmail(email, 4);

    assertEquals(expected, result);
  }

  @Test
  void encryptEmail_shouldEncryptWholeEmail_whenEmailLengthIsLowerThanVisiblePart() {
    var email = "user@example.com";
    var expected = "****@example.com";

    var result = StringUtils.encryptEmail(email, 5);

    assertEquals(expected, result);
  }

  @Test
  void encryptEmail_shouldThrow_whenNoAtCharacterFound() {
    assertThrows(InvalidEmailException.class, () -> StringUtils.encryptEmail("invalid-email", 3));
  }

  @Test
  void encryptEmail_shouldThrow_whenMoreThanOneAtCharacterFound() {
    assertThrows(
        InvalidEmailException.class, () -> StringUtils.encryptEmail("invalid@email@pl", 3));
  }

  @Test
  void encryptEmail_shouldThrow_whenAtIsLastCharacterFound() {
    assertThrows(InvalidEmailException.class, () -> StringUtils.encryptEmail("invalid-email@", 3));
  }

  @Test
  void encryptEmail_shouldThrow_whenVisibleCharsAreNegative() {
    assertThrows(
        IllegalArgumentException.class, () -> StringUtils.encryptEmail("invalid-email@", -1));
  }
}
