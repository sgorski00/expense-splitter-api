package pl.sgorski.expense_splitter.validator.text;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;

public class QueryValidatorTest {

  private final QueryValidator validator = new QueryValidator();
  private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

  @Test
  void isValid_shouldReturnTrue_whenValueNull() {
    assertTrue(validator.isValid(null, context));
  }

  @Test
  void isValid_shouldReturnTrue_whenValidQueryOnlyLetters() {
    var value = "test";

    assertTrue(validator.isValid(value, context));
  }

  @Test
  void isValid_shouldReturnTrue_whenValidQueryLettersAndNumbers() {
    var value = "test123";

    assertTrue(validator.isValid(value, context));
  }

  @Test
  void isValid_shouldReturnTrue_whenValidQueryLettersAndNumbersAndWhitespace() {
    var value = "test 123";

    assertTrue(validator.isValid(value, context));
  }

  @Test
  void isValid_shouldReturnTrue_whenValidQueryNumbers() {
    var value = "12345";

    assertTrue(validator.isValid(value, context));
  }

  @Test
  void isValid_shouldReturnTrue_whenQueryWithSpecialCharactersAndLetters() {
    var value = "test-query";

    assertTrue(validator.isValid(value, context));
  }

  @Test
  void isValid_shouldReturnTrue_whenQueryLengthExactly3() {
    var value = "abc";

    assertTrue(validator.isValid(value, context));
  }

  @Test
  void isValid_shouldReturnTrue_whenQueryLengthExactly100() {
    var value = "a".repeat(100);

    assertTrue(validator.isValid(value, context));
  }

  @Test
  void isValid_shouldReturnFalse_whenQueryLengthLessThan3() {
    var value = "ab";

    assertFalse(validator.isValid(value, context));
  }

  @Test
  void isValid_shouldReturnFalse_whenQueryLengthGreaterThan100() {
    var value = "a".repeat(101);

    assertFalse(validator.isValid(value, context));
  }

  @Test
  void isValid_shouldReturnFalse_whenQueryEmpty() {
    var value = "";

    assertFalse(validator.isValid(value, context));
  }

  @Test
  void isValid_shouldReturnFalse_whenQueryOnlySpecialCharacters() {
    var value = "____";

    assertFalse(validator.isValid(value, context));
  }

  @Test
  void isValid_shouldReturnFalse_whenQueryOnlyWhitespace() {
    var value = "   ";

    assertFalse(validator.isValid(value, context));
  }

  @Test
  void isValid_shouldReturnTrue_whenQueryWithWhitespaceBeforeAndAfter() {
    var value = "  test  ";

    assertTrue(validator.isValid(value, context));
  }
}
