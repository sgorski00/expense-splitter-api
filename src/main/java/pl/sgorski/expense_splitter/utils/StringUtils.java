package pl.sgorski.expense_splitter.utils;

import org.jspecify.annotations.Nullable;

public final class StringUtils {
  public static String requireString(@Nullable String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("String value cannot be null or blank");
    }
    return value;
  }
}
