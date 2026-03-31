package pl.sgorski.expense_splitter.features.auth.oauth2.provider.impl;

import lombok.Getter;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;
import pl.sgorski.expense_splitter.features.auth.oauth2.provider.OAuth2UserInfo;
import pl.sgorski.expense_splitter.utils.StringUtils;

import java.util.Map;

@Getter
public final class FacebookOAuth2UserInfo extends OAuth2UserInfo {

    public FacebookOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.provider = AuthProvider.FACEBOOK;
        this.providerId = StringUtils.requireString((String) attributes.get("id"));
        this.email = StringUtils.requireString((String) attributes.get("email"));
        this.firstName = (String) attributes.get("first_name");
        this.lastName = (String) attributes.get("last_name");
    }
}
