package pl.sgorski.expense_splitter.validator.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;
import pl.sgorski.expense_splitter.features.user.dto.contract.PasswordChange;

public class PasswordValidator implements ConstraintValidator<ValidPassword, PasswordChange> {

  /**
   * Regex explanation:
   *
   * <ul>
   *   <li>^: Start of string
   *   <li>(?=.*[A-Z]): At least one uppercase letter
   *   <li>(?=.*[a-z]): At least one lowercase letter
   *   <li>(?=.*\d): At least one digit
   *   <li>(?=.*[^a-zA-Z0-9]): At least one special character (non-alphanumeric)
   *   <li>(?=\S+$): No whitespace allowed
   *   <li>.{8,}: At least 8 characters long
   *   <li>$: End of string
   * </ul>
   */
  private static final Pattern PASSWORD_PATTERN =
      Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^a-zA-Z0-9])(?=\\S+$).{8,}$");

  @Override
  public boolean isValid(@Nullable PasswordChange request, ConstraintValidatorContext context) {
    if (request == null) {
      return true;
    }

    if (!request.newPassword().equals(request.repeatNewPassword())) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate("Passwords do not match")
          .addPropertyNode("repeatNewPassword")
          .addConstraintViolation();
      return false;
    }

    if (!PASSWORD_PATTERN.matcher(request.newPassword()).matches()) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate(
              "Password must be at least 8 chars, contain uppercase letter, digit and special character")
          .addPropertyNode("newPassword")
          .addConstraintViolation();
      return false;
    }

    return true;
  }
}
