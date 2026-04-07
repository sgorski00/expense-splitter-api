package pl.sgorski.expense_splitter.validator.text;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NullOrNotBlankValidator implements ConstraintValidator<NullOrNotBlank, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }

    return !value.isBlank();
  }
}
