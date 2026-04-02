package pl.sgorski.expense_splitter.validator.password;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

public class PasswordRulesTest {

  private final String validPassword = "V4lidP@ssw0rd";

  @Test
  void regex_shouldReturnTrue_whenPasswordIsValid() {
    assertTrue(Pattern.matches(PasswordRules.REGEX, validPassword));
  }

  @Test
  void regex_shouldReturnFalse_whenPasswordIsTooShort() {
    assertFalse(Pattern.matches(PasswordRules.REGEX, validPassword.substring(0, 7)));
  }

  @Test
  void regex_shouldReturnFalse_whenMissingUppercase() {
    assertFalse(Pattern.matches(PasswordRules.REGEX, validPassword.toLowerCase()));
  }

  @Test
  void regex_shouldReturnFalse_whenMissingLowercase() {
    assertFalse(Pattern.matches(PasswordRules.REGEX, validPassword.toUpperCase()));
  }

  @Test
  void regex_shouldReturnFalse_whenMissingSpecialCharacter() {
    assertFalse(Pattern.matches(PasswordRules.REGEX, "N0tValidP4ssw0rd"));
  }

  @Test
  void regex_shouldReturnFalse_whenMissingDigit() {
    assertFalse(Pattern.matches(PasswordRules.REGEX, "N@tValidP!ssw#rd"));
  }

  @Test
  void regex_shouldReturnFalse_whenContainsWhitespace() {
    assertFalse(Pattern.matches(PasswordRules.REGEX, "N0tValid Passw#rd"));
  }
}
