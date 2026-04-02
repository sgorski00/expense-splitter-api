package pl.sgorski.expense_splitter.security.authenticated;

import java.util.UUID;
import org.springframework.security.core.Authentication;
import pl.sgorski.expense_splitter.features.user.domain.User;

public interface AuthenticatedUserResolver {
  UUID requireUserId(Authentication authentication);

  User requireUser(Authentication authentication);
}
