package pl.sgorski.expense_splitter.validator.password;

public final class PasswordRules {
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
  public static final String REGEX =
      "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^a-zA-Z0-9])(?=\\S+$).{8,}$";

  private PasswordRules() {}
}
