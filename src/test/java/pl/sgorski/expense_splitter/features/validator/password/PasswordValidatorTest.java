package pl.sgorski.expense_splitter.features.validator.password;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintValidatorContext;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.sgorski.expense_splitter.features.user.dto.contract.PasswordChange;
import pl.sgorski.expense_splitter.validator.password.PasswordValidator;

public class PasswordValidatorTest {

  private PasswordValidator validator;
  private ConstraintValidatorContext context;
  private ConstraintValidatorContext.ConstraintViolationBuilder constraintViolation;
  private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext
      nodeBuilder;

  @BeforeEach
  void setUp() {
    context = mock(ConstraintValidatorContext.class);
    constraintViolation = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
    nodeBuilder =
        mock(
            ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext
                .class);
    validator = new PasswordValidator();
  }

  @Test
  void isValid_shouldReturnTrue_whenValidPassword() {
    var request = createPasswordChange("ValidP@ssw0rd", "ValidP@ssw0rd");
    assertTrue(validator.isValid(request, context));
  }

  @Test
  void isValid_shouldReturnTrue_whenNullRequest() {
    assertTrue(validator.isValid(null, context));
  }

  @Test
  void isValid_shouldReturnFalse_whenPasswordsDoNotMatch() {
    var request = createPasswordChange("ValidP@ssw0rd1", "ValidP@ssw0rd2");
    when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(constraintViolation);
    when(constraintViolation.addPropertyNode(anyString())).thenReturn(nodeBuilder);

    assertFalse(validator.isValid(request, context));
    verify(context).disableDefaultConstraintViolation();
  }

  @Test
  void isValid_shouldReturnFalse_whenPasswordTooShort() {
    var request = createPasswordChange("P@ss1", "P@ss1");
    when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(constraintViolation);
    when(constraintViolation.addPropertyNode(anyString())).thenReturn(nodeBuilder);

    assertFalse(validator.isValid(request, context));
    verify(context).disableDefaultConstraintViolation();
  }

  @Test
  void isValid_shouldReturnFalse_whenPasswordMissingUppercase() {
    var request = createPasswordChange("validp@ssw0rd", "validp@ssw0rd");
    when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(constraintViolation);
    when(constraintViolation.addPropertyNode(anyString())).thenReturn(nodeBuilder);

    assertFalse(validator.isValid(request, context));
    verify(context).disableDefaultConstraintViolation();
  }

  @Test
  void isValid_shouldReturnFalse_whenPasswordMissingLowercase() {
    var request = createPasswordChange("VALIDP@SSW0RD", "VALIDP@SSW0RD");
    when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(constraintViolation);
    when(constraintViolation.addPropertyNode(anyString())).thenReturn(nodeBuilder);

    assertFalse(validator.isValid(request, context));
    verify(context).disableDefaultConstraintViolation();
  }

  @Test
  void isValid_shouldReturnFalse_whenPasswordMissingDigit() {
    var request = createPasswordChange("ValidPassword@", "ValidPassword@");
    when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(constraintViolation);
    when(constraintViolation.addPropertyNode(anyString())).thenReturn(nodeBuilder);

    assertFalse(validator.isValid(request, context));
    verify(context).disableDefaultConstraintViolation();
  }

  @Test
  void isValid_shouldReturnFalse_whenPasswordMissingSpecialCharacter() {
    var request = createPasswordChange("ValidPassword0", "ValidPassword0");
    when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(constraintViolation);
    when(constraintViolation.addPropertyNode(anyString())).thenReturn(nodeBuilder);

    assertFalse(validator.isValid(request, context));
    verify(context).disableDefaultConstraintViolation();
  }

  @Test
  void isValid_shouldReturnFalse_whenPasswordContainsWhitespace() {
    var request = createPasswordChange("Valid P@ssw0rd", "Valid P@ssw0rd");
    when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(constraintViolation);
    when(constraintViolation.addPropertyNode(anyString())).thenReturn(nodeBuilder);

    assertFalse(validator.isValid(request, context));
    verify(context).disableDefaultConstraintViolation();
  }

  @NullMarked
  private PasswordChange createPasswordChange(String newPassword, String repeatPassword) {
    return new PasswordChange() {
      @Override
      public String newPassword() {
        return newPassword;
      }

      @Override
      public String repeatNewPassword() {
        return repeatPassword;
      }
    };
  }
}
