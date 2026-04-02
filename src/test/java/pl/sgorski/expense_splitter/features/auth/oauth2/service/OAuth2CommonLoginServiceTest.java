package pl.sgorski.expense_splitter.features.auth.oauth2.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;
import pl.sgorski.expense_splitter.exceptions.AccountLinkRequiredException;
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
  @Mock private AuthMapper authMapper;
  @Mock private UserService userService;
  @Mock private UserIdentityService userIdentityService;
  @InjectMocks private OAuth2CommonLoginService oAuth2CommonLoginService;

  private final AuthProvider provider = AuthProvider.FACEBOOK;
  private final String providerId = "1234567890";
  private final String email = "user@example.com";

  private UUID linkUserId;
  private OAuth2User oAuth2User;
  private OAuth2UserInfo userInfo;

  @BeforeEach
  void setUp() {
    linkUserId = UUID.randomUUID();

    oAuth2User = mock(OAuth2User.class);
    userInfo = mock(OAuth2UserInfo.class);
    when(userInfo.getProviderId()).thenReturn(providerId);
    when(userInfo.getProvider()).thenReturn(provider);
    when(userInfo.getEmail()).thenReturn(email);
  }

  @Test
  void handle_shouldRegisterAccount_correctRequestIdentityNotFound() {
    var context = new OAuth2LoginContext(oAuth2User, userInfo, true, linkUserId);
    when(userIdentityService.isUserIdentityPresent(eq(providerId), eq(provider))).thenReturn(false);
    when(userService.isUserPresent(eq(email))).thenReturn(false);
    when(authMapper.toIdentity(eq(userInfo))).thenReturn(new UserIdentity());

    var processedOAuth2User = oAuth2CommonLoginService.handle(context);

    assertEquals(oAuth2User, processedOAuth2User);
    verify(userIdentityService, times(1)).isUserIdentityPresent(providerId, provider);
    verify(userService, times(1)).isUserPresent(email);
    verify(authMapper, times(1)).toIdentity(userInfo);
    verify(userService, times(1)).save(any(User.class));
    verifyNoMoreInteractions(userService, userIdentityService, authMapper);
  }

  @Test
  void handle_shouldLoginWithPresentIdentity_correctRequestIdentityFound() {
    var context = new OAuth2LoginContext(oAuth2User, userInfo, true, linkUserId);
    when(userIdentityService.isUserIdentityPresent(eq(providerId), eq(provider))).thenReturn(true);

    var logged = oAuth2CommonLoginService.handle(context);

    assertEquals(oAuth2User, logged);
    verify(userIdentityService, times(1)).isUserIdentityPresent(eq(providerId), eq(provider));
    verifyNoMoreInteractions(userIdentityService);
    verifyNoInteractions(userService, authMapper);
  }

  @Test
  void handle_shouldThrowException_emailTakenByLocalUser() {
    var context = new OAuth2LoginContext(oAuth2User, userInfo, true, linkUserId);
    when(userIdentityService.isUserIdentityPresent(eq(providerId), eq(provider))).thenReturn(false);
    when(userService.isUserPresent(eq(email))).thenReturn(true);

    assertThrows(
        AccountLinkRequiredException.class, () -> oAuth2CommonLoginService.handle(context));
    verify(userIdentityService, times(1)).isUserIdentityPresent(eq(providerId), eq(provider));
    verify(userService, times(1)).isUserPresent(eq(email));
    verifyNoMoreInteractions(userIdentityService, userService);
    verifyNoInteractions(authMapper);
  }
}
