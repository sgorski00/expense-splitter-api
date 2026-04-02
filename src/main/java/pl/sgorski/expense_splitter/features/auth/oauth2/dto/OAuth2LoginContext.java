package pl.sgorski.expense_splitter.features.auth.oauth2.dto;

import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.springframework.security.oauth2.core.user.OAuth2User;
import pl.sgorski.expense_splitter.features.auth.oauth2.provider.OAuth2UserInfo;

public record OAuth2LoginContext(
    OAuth2User oauthUser, OAuth2UserInfo userInfo, boolean linkMode, @Nullable UUID linkUserId) {}
