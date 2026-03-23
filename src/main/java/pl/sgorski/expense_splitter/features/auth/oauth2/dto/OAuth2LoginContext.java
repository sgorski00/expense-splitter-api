package pl.sgorski.expense_splitter.features.auth.oauth2.dto;

import org.jspecify.annotations.Nullable;
import org.springframework.security.oauth2.core.user.OAuth2User;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;
import pl.sgorski.expense_splitter.features.auth.oauth2.provider.OAuth2UserInfo;

import java.util.UUID;

public record OAuth2LoginContext (
        OAuth2User oauthUser,
        AuthProvider provider,
        OAuth2UserInfo userInfo,
        boolean linkMode,
        @Nullable UUID linkUserId
){ }
