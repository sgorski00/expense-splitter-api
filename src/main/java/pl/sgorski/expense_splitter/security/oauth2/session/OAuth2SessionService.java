package pl.sgorski.expense_splitter.security.oauth2.session;

import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public final class OAuth2SessionService {
  public static final String OAUTH_MODE_KEY = "oauth_mode";
  public static final String OAUTH_LINK_USER_ID_KEY = "oauth_link_user_id";

  public boolean isLinkMode(HttpSession session) {
    var mode = session.getAttribute(OAUTH_MODE_KEY);
    if (mode instanceof String m) {
      return "link".equalsIgnoreCase(m.trim());
    }
    return false;
  }

  public @Nullable UUID getOAuthLinkUserId(HttpSession session) {
    return (UUID) session.getAttribute(OAUTH_LINK_USER_ID_KEY);
  }

  public void clearOAuthAttributes(HttpSession session) {
    session.removeAttribute(OAUTH_MODE_KEY);
    session.removeAttribute(OAUTH_LINK_USER_ID_KEY);
  }
}
