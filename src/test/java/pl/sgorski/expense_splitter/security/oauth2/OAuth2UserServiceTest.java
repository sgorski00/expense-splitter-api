package pl.sgorski.expense_splitter.security.oauth2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;
import pl.sgorski.expense_splitter.features.auth.oauth2.dto.OAuth2LoginContext;
import pl.sgorski.expense_splitter.features.auth.oauth2.factory.OAuth2UserInfoFactory;
import pl.sgorski.expense_splitter.features.auth.oauth2.provider.OAuth2UserInfo;
import pl.sgorski.expense_splitter.features.auth.oauth2.service.impl.OAuth2AccountLinkService;
import pl.sgorski.expense_splitter.features.auth.oauth2.service.impl.OAuth2CommonLoginService;
import pl.sgorski.expense_splitter.features.user.domain.User;

@ExtendWith(MockitoExtension.class)
public class OAuth2UserServiceTest {

  @Mock private OAuth2CommonLoginService oAuth2CommonLoginService;

  @Mock private OAuth2AccountLinkService oAuth2AccountLinkService;

  @Mock private OAuth2PayloadResolver oAuth2PayloadResolver;

  @Spy @InjectMocks private OAuth2UserServiceImpl oAuth2UserService;

  @Mock private OAuth2UserRequest userRequest;

  @Mock private OAuth2User providerUser;

  @Mock private User user;

  @Mock private OAuth2UserInfo userInfo;

  @BeforeEach
  void setUp() {
    var providerRegistrationId = "google";
    var clientRegistration = mock(ClientRegistration.class);
    doReturn(providerUser).when(oAuth2UserService).loadUserFromProvider(eq(userRequest));
    when(userRequest.getClientRegistration()).thenReturn(clientRegistration);
    when(clientRegistration.getRegistrationId()).thenReturn(providerRegistrationId);
  }

  @Test
  void loadUser_shouldHandleCommonLogin_whenNotInLinkMode() {
    try (var userFactory = mockStatic(OAuth2UserInfoFactory.class)) {
      userFactory
          .when(() -> OAuth2UserInfoFactory.create(any(AuthProvider.class), anyMap()))
          .thenReturn(userInfo);
      var payload = new OAuth2ContextPayload(UUID.randomUUID(), OAuth2Mode.LOGIN);
      when(oAuth2PayloadResolver.consume()).thenReturn(Optional.of(payload));
      when(oAuth2CommonLoginService.handle(any(OAuth2LoginContext.class))).thenReturn(user);

      var loadedUser = oAuth2UserService.loadUser(userRequest);

      assertEquals(providerUser, loadedUser);
      verify(oAuth2CommonLoginService, times(1)).handle(any(OAuth2LoginContext.class));
      verify(oAuth2PayloadResolver, times(1)).consume();
      verifyNoInteractions(oAuth2AccountLinkService);
    }
  }

  @Test
  void loadUser_shouldHandleAccountLink_whenInLinkMode() {
    try (var userFactory = mockStatic(OAuth2UserInfoFactory.class)) {
      userFactory
          .when(() -> OAuth2UserInfoFactory.create(any(AuthProvider.class), anyMap()))
          .thenReturn(userInfo);
      var payload = new OAuth2ContextPayload(UUID.randomUUID(), OAuth2Mode.LINK);
      when(oAuth2PayloadResolver.consume()).thenReturn(Optional.of(payload));
      when(oAuth2AccountLinkService.handle(any(OAuth2LoginContext.class))).thenReturn(user);

      var loadedUser = oAuth2UserService.loadUser(userRequest);

      assertEquals(providerUser, loadedUser);
      verify(oAuth2AccountLinkService, times(1)).handle(any(OAuth2LoginContext.class));
      verify(oAuth2PayloadResolver, times(1)).consume();
      verifyNoInteractions(oAuth2CommonLoginService);
    }
  }
}
