package pl.sgorski.expense_splitter.features.auth.oauth2.provider;

import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;

public interface OAuth2UserInfo {
    AuthProvider getProvider();
    String getProviderId();
    String getEmail();
    String getFirstName();
    String getLastName();
}
