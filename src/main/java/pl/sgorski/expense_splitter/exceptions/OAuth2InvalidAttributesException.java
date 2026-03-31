package pl.sgorski.expense_splitter.exceptions;

import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;

public class OAuth2InvalidAttributesException extends RuntimeException {
  public OAuth2InvalidAttributesException(AuthProvider provider, Throwable cause) {
    super("Invalid OAuth2 attributes for provider: " + provider, cause);
  }


}
