package pl.sgorski.expense_splitter.security.service.impl;

import java.util.Set;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.security.service.WhitelistService;

@Service
public class PasswordChangeRequiredWhitelistService implements WhitelistService {

  private static final Set<String> WHITELISTED_PATHS =
      Set.of("/api/profile/password", "/api/auth/logout", "/api/auth/refresh");

  @Override
  public boolean isWhitelisted(String path) {
    return WHITELISTED_PATHS.contains(path);
  }
}
