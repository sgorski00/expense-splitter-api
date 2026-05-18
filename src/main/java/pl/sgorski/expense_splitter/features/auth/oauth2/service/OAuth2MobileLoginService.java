package pl.sgorski.expense_splitter.features.auth.oauth2.service;

import pl.sgorski.expense_splitter.features.user.domain.User;

public interface OAuth2MobileLoginService {
  User handle(String idToken);
}
