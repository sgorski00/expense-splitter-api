package pl.sgorski.expense_splitter.validator.text;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class QueryValidator implements ConstraintValidator<ValidQuery, String> {

  private static final Pattern VALID_QUERY_PATTERN = Pattern.compile("^[^%_'\"]+$");

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) return true;
    var query = value.trim();
    if (query.length() < 3) return false;
    if (query.length() > 100) return false;
    return VALID_QUERY_PATTERN.matcher(query).matches();
  }
}
