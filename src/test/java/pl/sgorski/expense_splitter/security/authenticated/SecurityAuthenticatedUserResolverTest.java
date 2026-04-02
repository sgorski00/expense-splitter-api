package pl.sgorski.expense_splitter.security.authenticated;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.domain.UserIdentity;
import pl.sgorski.expense_splitter.features.user.service.UserIdentityService;
import pl.sgorski.expense_splitter.features.user.service.UserService;
import pl.sgorski.expense_splitter.security.authenticated.impl.SecurityAuthenticatedUserResolver;

import java.security.Principal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SecurityAuthenticatedUserResolverTest {

    @Mock
    private UserIdentityService userIdentityService;

    @Mock
    private UserService userService;

    @InjectMocks
    private SecurityAuthenticatedUserResolver authenticatedUserResolver;

    @Test
    public void requireUserId_shouldReturnUserId_whenInstanceOfUser() {
        var userId = UUID.randomUUID();
        var user = new User();
        user.setId(userId);
        var authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        var result = authenticatedUserResolver.requireUserId(authentication);

        assertEquals(userId, result);
    }

    @Test
    public void requireUserId_shouldReturnUserId_whenInstanceOfOAuth2Token() {
        var userId = UUID.randomUUID();
        var user = new User();
        user.setId(userId);
        var identity = new UserIdentity();
        identity.setUser(user);
        var authentication = mock(OAuth2AuthenticationToken.class);
        when(authentication.getName()).thenReturn("oauth2-user-id");
        when(authentication.getAuthorizedClientRegistrationId()).thenReturn("google");
        when(userIdentityService.findIdentity(any(AuthProvider.class), anyString())).thenReturn(identity);

        var result = authenticatedUserResolver.requireUserId(authentication);

        assertEquals(userId, result);
    }

    @Test
    public void requireUserId_shouldThrowIllegalStateException_whenNotRecognizedPrincipal() {
        var authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(Principal.class));

        assertThrows(IllegalStateException.class, () -> authenticatedUserResolver.requireUserId(authentication));
    }

    @Test
    public void requireUser_shouldReturnUser_whenInstanceOfUser() {
        var userId = UUID.randomUUID();
        var user = new User();
        user.setId(userId);
        var authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(userService.getUser(eq(userId))).thenReturn(user);

        var result = authenticatedUserResolver.requireUser(authentication);

        assertNotNull(result);
    }

    @Test
    public void requireUser_shouldReturnUser_whenInstanceOfOAuth2Token() {
        var userId = UUID.randomUUID();
        var user = new User();
        user.setId(userId);
        var identity = new UserIdentity();
        identity.setUser(user);
        var authentication = mock(OAuth2AuthenticationToken.class);
        when(authentication.getName()).thenReturn("oauth2-user-id");
        when(authentication.getAuthorizedClientRegistrationId()).thenReturn("google");
        when(userIdentityService.findIdentity(any(AuthProvider.class), anyString())).thenReturn(identity);
        when(userService.getUser(eq(userId))).thenReturn(user);

        var result = authenticatedUserResolver.requireUser(authentication);

        assertNotNull(result);
    }

    @Test
    public void requireUser_shouldThrowIllegalStateException_whenNotRecognizedPrincipal() {
        var authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(Principal.class));

        assertThrows(IllegalStateException.class, () -> authenticatedUserResolver.requireUser(authentication));
    }
}
