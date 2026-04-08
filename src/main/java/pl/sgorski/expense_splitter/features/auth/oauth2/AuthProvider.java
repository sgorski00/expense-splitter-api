package pl.sgorski.expense_splitter.features.auth.oauth2;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import pl.sgorski.expense_splitter.exceptions.authentication.ProviderNotFoundException;

public enum AuthProvider {
  GOOGLE,
  FACEBOOK;

  @JsonCreator
  public static AuthProvider fromString(String value) {
    return Arrays.stream(values())
        .filter(provider -> provider.name().equalsIgnoreCase(value.trim()))
        .findFirst()
        .orElseThrow(() -> new ProviderNotFoundException(value));
  }
}
