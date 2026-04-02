package pl.sgorski.expense_splitter.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

public class UuidUtilsTest {

  @Test
  void isValidUuid_shouldReturnTrue_forValidUuid() {
    var value = UUID.randomUUID().toString();

    var result = UuidUtils.isValidUuid(value);

    assertTrue(result);
  }

  @Test
  void isValidUuid_shouldReturnFalse_forInvalidUuid() {
    var value = "invalid-uuid";

    var result = UuidUtils.isValidUuid(value);

    assertFalse(result);
  }
}
