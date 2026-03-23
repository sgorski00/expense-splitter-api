package pl.sgorski.expense_splitter.security.authenticated;

import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface AuthenticatedUserResolver {
    UUID requireUserId(Authentication authentication);
}
