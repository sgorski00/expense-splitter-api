package pl.sgorski.expense_splitter.validator.text;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NullOrNotBlankValidator.class)
@Documented
public @interface NullOrNotBlank {
  String message() default "Field must be null or not blank";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
