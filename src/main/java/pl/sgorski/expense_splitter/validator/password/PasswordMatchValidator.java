package pl.sgorski.expense_splitter.validator.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import org.jspecify.annotations.Nullable;
import pl.sgorski.expense_splitter.features.user.dto.contract.PasswordChange;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, PasswordChange> {

  @Override
  public boolean isValid(@Nullable PasswordChange request, ConstraintValidatorContext context) {
    if (request == null) {
      return true;
    }

    if (!Objects.equals(request.newPassword(), request.repeatNewPassword())) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate("Passwords do not match")
          .addPropertyNode("repeatNewPassword")
          .addConstraintViolation();
      return false;
    }

    return true;
  }
}
