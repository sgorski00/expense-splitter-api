package pl.sgorski.expense_splitter.features.auth.oauth2.service;

import org.springframework.security.oauth2.core.user.OAuth2User;
import pl.sgorski.expense_splitter.features.auth.oauth2.dto.OAuth2LoginContext;

public interface OAuth2LoginService {
    OAuth2User handle(OAuth2LoginContext context);
}
