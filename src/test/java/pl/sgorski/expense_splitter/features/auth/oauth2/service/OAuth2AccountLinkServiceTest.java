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
import pl.sgorski.expense_splitter.exceptions.authentication.AccountLinkingException;
import pl.sgorski.expense_splitter.features.auth.mapper.AuthMapper;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;
import pl.sgorski.expense_splitter.features.auth.oauth2.dto.OAuth2LoginContext;
import pl.sgorski.expense_splitter.features.auth.oauth2.provider.OAuth2UserInfo;
import pl.sgorski.expense_splitter.features.auth.oauth2.service.impl.OAuth2AccountLinkService;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.domain.UserIdentity;
import pl.sgorski.expense_splitter.features.user.service.UserIdentityService;
import pl.sgorski.expense_splitter.features.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class OAuth2AccountLinkServiceTest {
  @Mock private AuthMapper authMapper; // TODO: replace with real mapper
  @Mock private UserService userService;
  @Mock private UserIdentityService userIdentityService;
  @InjectMocks private OAuth2AccountLinkService oAuth2AccountLinkService;

  private final AuthProvider provider = AuthProvider.FACEBOOK;
  private final String providerId = "1234567890";
  private User existingUser;
  private UserIdentity newIdentity;
  private UUID linkUserId;
  private OAuth2User oAuth2User;
  private OAuth2UserInfo userInfo;

  @BeforeEach
  void setUp() {
    linkUserId = UUID.randomUUID();
    existingUser = new User();
    newIdentity = new UserIdentity();

    oAuth2User = mock(OAuth2User.class);
    userInfo = mock(OAuth2UserInfo.class);
    when(userInfo.getProviderId()).thenReturn(providerId);
    when(userInfo.getProvider()).thenReturn(provider);
  }

  @Test
  void handle_shouldLinkAccount_correctRequest() {
    var context = new OAuth2LoginContext(oAuth2User, userInfo, true, linkUserId);
    when(userIdentityService.isUserIdentityPresent(eq(providerId), eq(provider))).thenReturn(false);
    when(userService.getUserWithIdentities(linkUserId)).thenReturn(existingUser);
    when(authMapper.toIdentity(eq(userInfo))).thenReturn(newIdentity);

    var processedOAuth2User = oAuth2AccountLinkService.handle(context);

    assertEquals(oAuth2User, processedOAuth2User);
    assertTrue(existingUser.getIdentities().contains(newIdentity));
    verify(userIdentityService, times(1)).isUserIdentityPresent(providerId, provider);
    verify(userService, times(1)).getUserWithIdentities(linkUserId);
    verify(authMapper, times(1)).toIdentity(userInfo);
    verify(userService, times(1)).save(existingUser);
    verifyNoMoreInteractions(userService, userIdentityService, authMapper);
  }

  @Test
  void handle_shouldThrowException_accountAlreadyLinked() {
    var context = new OAuth2LoginContext(oAuth2User, userInfo, true, linkUserId);
    when(userIdentityService.isUserIdentityPresent(eq(providerId), eq(provider))).thenReturn(true);

    assertThrows(AccountLinkingException.class, () -> oAuth2AccountLinkService.handle(context));
    verify(userIdentityService, times(1)).isUserIdentityPresent(eq(providerId), eq(provider));
    verifyNoMoreInteractions(userIdentityService);
    verifyNoInteractions(userService, authMapper);
  }

  @Test
  void handle_shouldThrowException_userIdNotPresent() {
    var context = new OAuth2LoginContext(oAuth2User, userInfo, true, null);
    when(userIdentityService.isUserIdentityPresent(eq(providerId), eq(provider))).thenReturn(false);

    assertThrows(AccountLinkingException.class, () -> oAuth2AccountLinkService.handle(context));
    verify(userIdentityService, times(1)).isUserIdentityPresent(eq(providerId), eq(provider));
    verifyNoMoreInteractions(userIdentityService);
    verifyNoInteractions(userService, authMapper);
  }
}
