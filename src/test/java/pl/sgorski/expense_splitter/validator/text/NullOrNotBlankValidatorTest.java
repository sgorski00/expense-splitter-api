package pl.sgorski.expense_splitter.validator.text;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;

public class NullOrNotBlankValidatorTest {

  private final NullOrNotBlankValidator validator = new NullOrNotBlankValidator();
  private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

  @Test
  void isValid_shouldReturnTrue_whenValueNotBlank() {
    var value = "abc";
    assertTrue(validator.isValid(value, context));
  }

  @Test
  void isValid_shouldReturnTrue_whenValueNull() {
    assertTrue(validator.isValid(null, context));
  }

  @Test
  void isValid_shouldReturnFalse_whenValueEmpty() {
    var value = "";

    assertFalse(validator.isValid(value, context));
  }

  @Test
  void isValid_shouldReturnFalse_whenValueBlank() {
    var value = " ";

    assertFalse(validator.isValid(value, context));
  }
}
