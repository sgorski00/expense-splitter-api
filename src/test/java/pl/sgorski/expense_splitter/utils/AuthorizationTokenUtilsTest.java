package pl.sgorski.expense_splitter.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class AuthorizationTokenUtilsTest {

  @Test
  void getTokenFromHeader_shouldReturnToken_whenHeaderIsValid() {
    var expected = "valid-token";
    var header = AuthorizationTokenUtils.BEARER_PREFIX + expected;

    var result = AuthorizationTokenUtils.getTokenFromHeader(header);

    assertEquals(expected, result);
  }

  @Test
  void getTokenFromHeader_shouldReturnNull_whenHeaderIsNull() {
    var result = AuthorizationTokenUtils.getTokenFromHeader(null);

    assertNull(result);
  }

  @Test
  void getTokenFromHeader_shouldReturnNull_whenHeaderDontStartWithBearer() {
    var header = "NOT-A-BEARER valid-token";

    var result = AuthorizationTokenUtils.getTokenFromHeader(header);

    assertNull(result);
  }

  @Test
  void getTokenFromHeader_shouldReturnNull_whenTokenIsEmpty() {
    var header = AuthorizationTokenUtils.BEARER_PREFIX;

    var result = AuthorizationTokenUtils.getTokenFromHeader(header);

    assertNull(result);
  }

  @Test
  void getTokenFromHeader_shouldReturnNull_whenTokenIsBlank() {
    var header = AuthorizationTokenUtils.BEARER_PREFIX + "    ";

    var result = AuthorizationTokenUtils.getTokenFromHeader(header);

    assertNull(result);
  }
}
