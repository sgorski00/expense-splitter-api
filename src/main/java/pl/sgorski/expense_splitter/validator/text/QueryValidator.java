package pl.sgorski.expense_splitter.validator.text;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class QueryValidator implements ConstraintValidator<ValidQuery, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) return true;
    var query = value.trim();
    if (query.length() < 3) return false;
    if (query.length() > 100) return false;
    return query.chars().anyMatch(Character::isLetterOrDigit);
  }
}
