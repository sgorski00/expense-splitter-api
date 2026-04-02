package pl.sgorski.expense_splitter.utils;

import org.jspecify.annotations.Nullable;
import pl.sgorski.expense_splitter.exceptions.InvalidEmailException;

public final class StringUtils {
  public static boolean isBlank(@Nullable String value) {
    return value == null || value.isBlank();
  }

  public static String requireString(@Nullable String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("String value cannot be null or blank");
    }
    return value;
  }

  public static String encryptEmail(String email, int visibleChars) {
    validateEmail(email);
    var localPart = email.substring(0, email.indexOf('@'));
    var domainPart = email.substring(email.indexOf('@'));
    if (localPart.length() <= visibleChars) {
      return localPart.replaceAll(".", "*") + domainPart;
    }
    var visiblePart = localPart.substring(0, visibleChars);
    var encryptedPart = localPart.substring(visibleChars).replaceAll(".", "*");
    return visiblePart + encryptedPart + domainPart;
  }

  private static void validateEmail(String email) {
    requireString(email);
    if (!email.contains("@")) {
      throw new InvalidEmailException(email);
    }
  }
}
