package pl.sgorski.expense_splitter.exceptions;

import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;

/** Thrown when the attributes provided by an OAuth2 provider are invalid or incomplete. */
public class OAuth2InvalidAttributesException extends RuntimeException {
  public OAuth2InvalidAttributesException(AuthProvider provider, Throwable cause) {
    super("Invalid OAuth2 attributes for provider: " + provider, cause);
  }
}
