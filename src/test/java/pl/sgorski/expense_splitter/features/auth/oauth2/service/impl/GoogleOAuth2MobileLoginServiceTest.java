package pl.sgorski.expense_splitter.features.auth.oauth2.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;
import pl.sgorski.expense_splitter.features.auth.oauth2.config.GoogleOAuth2Properties;
import pl.sgorski.expense_splitter.features.auth.oauth2.dto.OAuth2LoginContext;
import pl.sgorski.expense_splitter.features.user.domain.User;

@ExtendWith(MockitoExtension.class)
class GoogleOAuth2MobileLoginServiceTest {

  @Mock private JwtDecoder jwtDecoder;
  @Mock private GoogleOAuth2Properties googleOAuth2Properties;
  @Mock private OAuth2CommonLoginService oAuth2CommonLoginService;
  @InjectMocks private GoogleOAuth2MobileLoginService googleOAuth2MobileLoginService;

  private final String clientId = "test-client-id";
  private final String idToken = "test.id.token";
  private final String googleIssuerUrl = "https://accounts.google.com";

  private User mockUser;

  @BeforeEach
  void setUp() {
    when(googleOAuth2Properties.clientId()).thenReturn(clientId);

    mockUser = new User();
    mockUser.setId(UUID.randomUUID());
    mockUser.setEmail("user@example.com");
  }

  @Test
  void handle_shouldReturnUser_whenJwtIsValid() throws Exception {
    var jwt = mock(Jwt.class);
    when(jwt.getAudience()).thenReturn(List.of(clientId));
    when(jwt.getIssuer()).thenReturn(new URL(googleIssuerUrl));
    when(jwt.getClaims())
        .thenReturn(
            Map.of(
                "sub", "123456789",
                "email", "user@example.com",
                "given_name", "John",
                "family_name", "Doe"));

    when(jwtDecoder.decode(idToken)).thenReturn(jwt);
    when(oAuth2CommonLoginService.handle(any(OAuth2LoginContext.class))).thenReturn(mockUser);

    var result = googleOAuth2MobileLoginService.handle(idToken);

    assertEquals(mockUser, result);
    verify(jwtDecoder, times(1)).decode(idToken);
    verify(oAuth2CommonLoginService, times(1)).handle(any(OAuth2LoginContext.class));
  }

  @Test
  void handle_shouldPassCorrectContextToCommonLoginService_withLinkModeFalseAndNullUserId()
      throws Exception {
    var jwt = mock(Jwt.class);
    when(jwt.getAudience()).thenReturn(List.of(clientId));
    when(jwt.getIssuer()).thenReturn(new URL(googleIssuerUrl));
    when(jwt.getClaims())
        .thenReturn(
            Map.<String, Object>of(
                "sub", "123456789",
                "email", "user@example.com",
                "given_name", "John",
                "family_name", "Doe"));

    when(jwtDecoder.decode(idToken)).thenReturn(jwt);
    when(oAuth2CommonLoginService.handle(any(OAuth2LoginContext.class))).thenReturn(mockUser);

    var contextCaptor = ArgumentCaptor.forClass(OAuth2LoginContext.class);

    googleOAuth2MobileLoginService.handle(idToken);

    verify(oAuth2CommonLoginService).handle(contextCaptor.capture());
    var context = contextCaptor.getValue();

    assertFalse(context.linkMode());
    assertNull(context.linkUserId());
    assertEquals(AuthProvider.GOOGLE, context.userInfo().getProvider());
  }

  @Test
  void handle_shouldThrowException_whenAudienceIsInvalid() throws Exception {
    var jwt = mock(Jwt.class);
    when(jwt.getAudience()).thenReturn(List.of("wrong-client-id"));

    when(jwtDecoder.decode(idToken)).thenReturn(jwt);

    assertThrows(
        OAuth2AuthenticationException.class, () -> googleOAuth2MobileLoginService.handle(idToken));
    verifyNoInteractions(oAuth2CommonLoginService);
  }

  @Test
  void handle_shouldThrowException_whenAudienceNull() throws Exception {
    var jwt = mock(Jwt.class);
    when(jwt.getAudience()).thenReturn(null);

    when(jwtDecoder.decode(idToken)).thenReturn(jwt);

    assertThrows(NullPointerException.class, () -> googleOAuth2MobileLoginService.handle(idToken));
    verifyNoInteractions(oAuth2CommonLoginService);
  }

  @Test
  void handle_shouldThrowException_whenIssuerIsInvalid() throws Exception {
    var jwt = mock(Jwt.class);
    when(jwt.getAudience()).thenReturn(List.of(clientId));
    when(jwt.getIssuer()).thenReturn(new URL("https://invalid-issuer.com"));

    when(jwtDecoder.decode(idToken)).thenReturn(jwt);

    assertThrows(
        OAuth2AuthenticationException.class, () -> googleOAuth2MobileLoginService.handle(idToken));
    verifyNoInteractions(oAuth2CommonLoginService);
  }

  @Test
  void handle_shouldThrowException_whenIssuerIsNull() throws Exception {
    var jwt = mock(Jwt.class);
    when(jwt.getAudience()).thenReturn(List.of(clientId));
    when(jwt.getIssuer()).thenReturn(null);

    when(jwtDecoder.decode(idToken)).thenReturn(jwt);

    assertThrows(
        OAuth2AuthenticationException.class, () -> googleOAuth2MobileLoginService.handle(idToken));
    verifyNoInteractions(oAuth2CommonLoginService);
  }

  @Test
  void handle_shouldAcceptValidGoogleIssuersFormats_withHttpsPrefix() throws Exception {
    var jwt = mock(Jwt.class);
    when(jwt.getAudience()).thenReturn(List.of(clientId));
    var mockIssuer = mock(URL.class);
    when(mockIssuer.toString()).thenReturn("https://accounts.google.com");
    when(jwt.getIssuer()).thenReturn(mockIssuer);
    when(jwt.getClaims())
        .thenReturn(
            Map.<String, Object>of(
                "sub", "123456789",
                "email", "user@example.com",
                "given_name", "John",
                "family_name", "Doe"));

    when(jwtDecoder.decode(idToken)).thenReturn(jwt);
    when(oAuth2CommonLoginService.handle(any(OAuth2LoginContext.class))).thenReturn(mockUser);

    var result = googleOAuth2MobileLoginService.handle(idToken);

    assertEquals(mockUser, result);
    verify(oAuth2CommonLoginService, times(1)).handle(any(OAuth2LoginContext.class));
  }

  @Test
  void handle_shouldAcceptValidGoogleIssuersFormats_withoutHttpsPrefix() throws Exception {
    var jwt = mock(Jwt.class);
    when(jwt.getAudience()).thenReturn(List.of(clientId));
    var mockIssuer = mock(URL.class);
    when(mockIssuer.toString()).thenReturn("accounts.google.com");
    when(jwt.getIssuer()).thenReturn(mockIssuer);
    when(jwt.getClaims())
        .thenReturn(
            Map.<String, Object>of(
                "sub", "123456789",
                "email", "user@example.com",
                "given_name", "John",
                "family_name", "Doe"));

    when(jwtDecoder.decode(idToken)).thenReturn(jwt);
    when(oAuth2CommonLoginService.handle(any(OAuth2LoginContext.class))).thenReturn(mockUser);

    var result = googleOAuth2MobileLoginService.handle(idToken);

    assertEquals(mockUser, result);
    verify(oAuth2CommonLoginService, times(1)).handle(any(OAuth2LoginContext.class));
  }

  @Test
  void handle_shouldCreateOAuth2UserInfo_fromDecodedJwtClaims() throws Exception {
    var claims =
        Map.<String, Object>of(
            "sub", "123456789",
            "email", "user@example.com",
            "given_name", "John",
            "family_name", "Doe");

    var jwt = mock(Jwt.class);
    when(jwt.getAudience()).thenReturn(List.of(clientId));
    when(jwt.getIssuer()).thenReturn(new URL(googleIssuerUrl));
    when(jwt.getClaims()).thenReturn(claims);

    when(jwtDecoder.decode(idToken)).thenReturn(jwt);
    when(oAuth2CommonLoginService.handle(any(OAuth2LoginContext.class))).thenReturn(mockUser);

    var contextCaptor = ArgumentCaptor.forClass(OAuth2LoginContext.class);

    googleOAuth2MobileLoginService.handle(idToken);

    verify(oAuth2CommonLoginService).handle(contextCaptor.capture());
    var context = contextCaptor.getValue();

    assertEquals("user@example.com", context.userInfo().getEmail());
    assertEquals("123456789", context.userInfo().getProviderId());
    assertEquals("John", context.userInfo().getFirstName());
    assertEquals("Doe", context.userInfo().getLastName());
  }

  @Test
  void handle_shouldPassJwtClaimsToOAuth2UserInfoFactory() throws Exception {
    var claims = Map.<String, Object>of("sub", "google-user-123", "email", "test@gmail.com");

    var jwt = mock(Jwt.class);
    when(jwt.getAudience()).thenReturn(List.of(clientId));
    when(jwt.getIssuer()).thenReturn(new URL(googleIssuerUrl));
    when(jwt.getClaims()).thenReturn(claims);

    when(jwtDecoder.decode(idToken)).thenReturn(jwt);
    when(oAuth2CommonLoginService.handle(any(OAuth2LoginContext.class))).thenReturn(mockUser);

    var contextCaptor = ArgumentCaptor.forClass(OAuth2LoginContext.class);

    googleOAuth2MobileLoginService.handle(idToken);

    verify(oAuth2CommonLoginService).handle(contextCaptor.capture());
    var context = contextCaptor.getValue();

    assertEquals(AuthProvider.GOOGLE, context.userInfo().getProvider());
    assertEquals("test@gmail.com", context.userInfo().getEmail());
  }
}
