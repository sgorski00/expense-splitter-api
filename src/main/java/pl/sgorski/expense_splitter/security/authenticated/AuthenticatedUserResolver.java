package pl.sgorski.expense_splitter.security.authenticated;

import org.springframework.security.core.Authentication;
import pl.sgorski.expense_splitter.features.user.domain.User;

import java.util.UUID;

public interface AuthenticatedUserResolver {
    UUID requireUserId(Authentication authentication);
    User requireUser(Authentication authentication);
}
