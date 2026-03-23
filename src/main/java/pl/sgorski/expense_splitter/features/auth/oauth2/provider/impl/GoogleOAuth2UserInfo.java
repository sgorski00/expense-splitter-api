package pl.sgorski.expense_splitter.features.auth.oauth2.provider.impl;

import lombok.Getter;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;
import pl.sgorski.expense_splitter.features.auth.oauth2.provider.OAuth2UserInfo;

import java.util.Map;

@Getter
public final class GoogleOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;
    private final AuthProvider provider;
    private final String providerId;
    private final String email;
    private final String firstName;
    private final String lastName;

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.provider = AuthProvider.GOOGLE;
        this.providerId = (String) attributes.get("sub");
        this.email = (String) attributes.get("email");
        this.firstName = (String) attributes.get("given_name");
        this.lastName = (String) attributes.get("family_name");
    }
}
