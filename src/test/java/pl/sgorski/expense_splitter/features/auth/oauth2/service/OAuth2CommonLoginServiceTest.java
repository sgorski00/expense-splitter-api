package pl.sgorski.expense_splitter.features.auth.oauth2.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.expense_splitter.exceptions.authentication.AccountLinkRequiredException;
import pl.sgorski.expense_splitter.features.auth.mapper.AuthMapper;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;
import pl.sgorski.expense_splitter.features.auth.oauth2.dto.OAuth2LoginContext;
import pl.sgorski.expense_splitter.features.auth.oauth2.provider.OAuth2UserInfo;
import pl.sgorski.expense_splitter.features.auth.oauth2.service.impl.OAuth2CommonLoginService;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.domain.UserIdentity;
import pl.sgorski.expense_splitter.features.user.service.UserIdentityService;
import pl.sgorski.expense_splitter.features.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class OAuth2CommonLoginServiceTest {
  @Mock private UserService userService;
  @Mock private UserIdentityService userIdentityService;
  private OAuth2CommonLoginService oAuth2CommonLoginService;

  private final AuthProvider provider = AuthProvider.FACEBOOK;
  private final String providerId = "1234567890";
  private final String email = "user@example.com";

  private UUID linkUserId;
  private User registeredUser;
  private User linkedUser;
  private UserIdentity existingIdentity;
  private OAuth2UserInfo userInfo;

  @BeforeEach
  void setUp() {
    linkUserId = UUID.randomUUID();
    registeredUser = new User();
    linkedUser = new User();
    existingIdentity = new UserIdentity();
    existingIdentity.setUser(linkedUser);

    userInfo = mock(OAuth2UserInfo.class);
    when(userInfo.getProviderId()).thenReturn(providerId);
    when(userInfo.getProvider()).thenReturn(provider);
    when(userInfo.getEmail()).thenReturn(email);

    var authMapper = Mappers.getMapper(AuthMapper.class);
    oAuth2CommonLoginService =
        new OAuth2CommonLoginService(authMapper, userService, userIdentityService);
  }

  @Test
  void handle_shouldRegisterAccount_correctRequestIdentityNotFound() {
    var context = new OAuth2LoginContext(userInfo, true, linkUserId);
    when(userIdentityService.isUserIdentityPresent(eq(providerId), eq(provider))).thenReturn(false);
    when(userService.isUserPresent(eq(email))).thenReturn(false);
    when(userService.save(any(User.class))).thenReturn(registeredUser);

    var processedUser = oAuth2CommonLoginService.handle(context);

    assertEquals(registeredUser, processedUser);
    verify(userIdentityService, times(1)).isUserIdentityPresent(providerId, provider);
    verify(userService, times(1)).isUserPresent(email);
    verify(userService, times(1)).save(any(User.class));
    verifyNoMoreInteractions(userService, userIdentityService);
  }

  @Test
  void handle_shouldLoginWithPresentIdentity_correctRequestIdentityFound() {
    var context = new OAuth2LoginContext(userInfo, true, linkUserId);
    when(userIdentityService.isUserIdentityPresent(eq(providerId), eq(provider))).thenReturn(true);
    when(userIdentityService.findIdentity(eq(provider), eq(providerId)))
        .thenReturn(existingIdentity);

    var loggedUser = oAuth2CommonLoginService.handle(context);

    assertEquals(linkedUser, loggedUser);
    verify(userIdentityService, times(1)).isUserIdentityPresent(eq(providerId), eq(provider));
    verify(userIdentityService, times(1)).findIdentity(eq(provider), eq(providerId));
    verifyNoMoreInteractions(userIdentityService);
    verifyNoInteractions(userService);
  }

  @Test
  void handle_shouldThrowException_emailTakenByLocalUser() {
    var context = new OAuth2LoginContext(userInfo, true, linkUserId);
    when(userIdentityService.isUserIdentityPresent(eq(providerId), eq(provider))).thenReturn(false);
    when(userService.isUserPresent(eq(email))).thenReturn(true);

    assertThrows(
        AccountLinkRequiredException.class, () -> oAuth2CommonLoginService.handle(context));
    verify(userIdentityService, times(1)).isUserIdentityPresent(eq(providerId), eq(provider));
    verify(userService, times(1)).isUserPresent(eq(email));
    verifyNoMoreInteractions(userIdentityService, userService);
  }
}
