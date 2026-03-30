package pl.sgorski.expense_splitter.security.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import pl.sgorski.expense_splitter.exceptions.IdentityNotFoundException;
import pl.sgorski.expense_splitter.features.auth.dto.response.LoginResponse;
import pl.sgorski.expense_splitter.features.auth.local.utils.TokenResponseEntityCreator;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;
import pl.sgorski.expense_splitter.features.auth.oauth2.factory.OAuth2UserInfoFactory;
import pl.sgorski.expense_splitter.features.auth.oauth2.provider.OAuth2UserInfo;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.domain.UserIdentity;
import pl.sgorski.expense_splitter.features.user.service.UserIdentityService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OAuth2SuccessHandlerTest {

    @Mock
    private UserIdentityService userIdentityService;

    @Mock
    private TokenResponseEntityCreator tokenResponseEntityCreator;

    private OAuth2SuccessHandler handler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private OAuth2AuthenticationToken authentication;

    @Mock
    private OAuth2UserInfo userInfo;

    @Mock
    private OAuth2User principal;

    @BeforeEach
    void setUp() throws Exception {
        handler = new OAuth2SuccessHandler(userIdentityService, tokenResponseEntityCreator, new ObjectMapper());
        when(authentication.getAuthorizedClientRegistrationId()).thenReturn("google");
        when(authentication.getPrincipal()).thenReturn(principal);
        when(userInfo.getProvider()).thenReturn(AuthProvider.GOOGLE);
        when(userInfo.getProviderId()).thenReturn("id");
    }

    @Test
    void onAuthenticationSuccess_shouldReturnLoginResponse_whenRequestIsSuccessful() throws Exception {
        var refreshToken = "test-refresh-token";
        var accessToken = "test-token";
        var loginResponse = new LoginResponse(accessToken, UUID.randomUUID());
        var responseEntity = ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshToken)
                .body(loginResponse);
        var identity = new UserIdentity();
        identity.setUser(new User());
        var writer = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(writer));
        when(principal.getAttributes()).thenReturn(Map.of());
        when(tokenResponseEntityCreator.generate(any(User.class))).thenReturn(responseEntity);
        when(userIdentityService.findIdentity(any(AuthProvider.class), anyString())).thenReturn(identity);

        try(var userInfoFactory = mockStatic(OAuth2UserInfoFactory.class)) {
            userInfoFactory
                    .when(() -> OAuth2UserInfoFactory.create(any(AuthProvider.class), any()))
                    .thenReturn(userInfo);

            handler.onAuthenticationSuccess(request, response, authentication);
        }
        
        assertTrue(writer.toString().contains(accessToken));
        verify(response).setStatus(eq(200));
        verify(response).addHeader(eq(HttpHeaders.SET_COOKIE), eq(refreshToken));
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(userIdentityService).findIdentity(any(AuthProvider.class), anyString());
    }

    @Test
    void onAuthenticationSuccess_shouldThrowNullPointer_whenResponseBodyIsNull() {
        var responseEntity = ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, "test-refresh-token")
                .body(null);
        var identity = new UserIdentity();
        identity.setUser(new User());
        doReturn(responseEntity).when(tokenResponseEntityCreator).generate(any(User.class));
        when(userIdentityService.findIdentity(any(AuthProvider.class), anyString())).thenReturn(identity);

        try(var userInfoFactory = mockStatic(OAuth2UserInfoFactory.class)) {
            userInfoFactory
                    .when(() -> OAuth2UserInfoFactory.create(any(AuthProvider.class), any()))
                    .thenReturn(userInfo);

            assertThrows(NullPointerException.class, () -> handler.onAuthenticationSuccess(request, response, authentication));
        }
    }

    @Test
    void onAuthenticationSuccess_shouldThrowAccessDeniedException_whenIdentityNotFound() {
        when(userIdentityService.findIdentity(any(AuthProvider.class), anyString())).thenThrow(IdentityNotFoundException.class);

        try(var userInfoFactory = mockStatic(OAuth2UserInfoFactory.class)) {
            userInfoFactory
                    .when(() -> OAuth2UserInfoFactory.create(any(AuthProvider.class), any()))
                    .thenReturn(userInfo);

            assertThrows(AccessDeniedException.class, () -> handler.onAuthenticationSuccess(request, response, authentication));
        }
    }
}
