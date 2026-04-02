package pl.sgorski.expense_splitter.validator.password;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Documented
@Pattern(regexp = PasswordRules.REGEX)
public @interface StrongPassword {
  String message() default
      "Password must be at least 8 chars, contain uppercase, lowercase, digit and special character";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
