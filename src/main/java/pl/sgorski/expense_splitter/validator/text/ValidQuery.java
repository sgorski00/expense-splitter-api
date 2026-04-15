package pl.sgorski.expense_splitter.validator.text;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = QueryValidator.class)
public @interface ValidQuery {
  String message() default "Invalid search query";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
