package pl.sgorski.expense_splitter.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class StringUtilsTest {

  @Test
  void require_shouldReturnString_whenNotBlank() {
    var value = ".";

    var result = StringUtils.requireString(value);

    assertEquals(value, result);
  }

  @Test
  void require_shouldThrowIllegalArgumentException_whenValueIsBlank() {
    var value = " ";

    assertThrows(IllegalArgumentException.class, () -> StringUtils.requireString(value));
  }

  @Test
  void require_shouldThrowIllegalArgumentException_whenValueIsEmpty() {
    var value = "";

    assertThrows(IllegalArgumentException.class, () -> StringUtils.requireString(value));
  }

  @Test
  void require_shouldThrowIllegalArgumentException_whenValueIsNull() {
    assertThrows(IllegalArgumentException.class, () -> StringUtils.requireString(null));
  }
}
