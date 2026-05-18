package pl.sgorski.expense_splitter.features.auth.oauth2.service;

import pl.sgorski.expense_splitter.features.auth.oauth2.dto.OAuth2LoginContext;
import pl.sgorski.expense_splitter.features.user.domain.User;

public interface OAuth2LoginService {
  User handle(OAuth2LoginContext context);
}
