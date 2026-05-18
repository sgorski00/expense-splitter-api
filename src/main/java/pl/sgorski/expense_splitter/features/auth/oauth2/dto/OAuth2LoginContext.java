package pl.sgorski.expense_splitter.features.auth.oauth2.dto;

import java.util.UUID;
import org.jspecify.annotations.Nullable;
import pl.sgorski.expense_splitter.features.auth.oauth2.provider.OAuth2UserInfo;

public record OAuth2LoginContext(
    OAuth2UserInfo userInfo, boolean linkMode, @Nullable UUID linkUserId) {}
