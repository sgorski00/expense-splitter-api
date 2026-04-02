package pl.sgorski.expense_splitter.utils;

import org.jspecify.annotations.Nullable;
import pl.sgorski.expense_splitter.exceptions.InvalidEmailException;

public final class StringUtils {

  public static String requireString(@Nullable String value) throws IllegalArgumentException {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("String value cannot be null or blank");
    }
    return value;
  }

  public static String encryptEmail(String email, int visibleChars)
      throws IllegalArgumentException {
    if (visibleChars < 0) {
      throw new IllegalArgumentException("visibleChars cannot be negative");
    }
    var atIndex = requireAtIndex(email);
    var localPart = email.substring(0, atIndex);
    var domainPart = email.substring(atIndex);
    if (localPart.length() <= visibleChars) {
      return "*".repeat(localPart.length()) + domainPart;
    }
    var visiblePart = localPart.substring(0, visibleChars);
    var encryptedPart = "*".repeat(localPart.length() - visibleChars);
    return visiblePart + encryptedPart + domainPart;
  }

  private static int requireAtIndex(String email) throws InvalidEmailException {
    requireString(email);
    var atIndex = email.indexOf('@');
    if (atIndex <= 0 || atIndex != email.lastIndexOf('@') || atIndex == email.length() - 1) {
      throw new InvalidEmailException(email);
    }
    return atIndex;
  }
}
