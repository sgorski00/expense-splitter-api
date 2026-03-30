package pl.sgorski.expense_splitter.features.auth.local.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import pl.sgorski.expense_splitter.features.auth.dto.response.LoginResponse;
import pl.sgorski.expense_splitter.features.auth.refresh_token.domain.RefreshToken;
import pl.sgorski.expense_splitter.features.auth.refresh_token.service.RefreshTokenCookieResponseHelper;
import pl.sgorski.expense_splitter.features.auth.refresh_token.service.RefreshTokenService;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.security.jwt.JwtService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TokenResponseEntityCreatorTest {

    @Mock private JwtService jwtService;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private RefreshTokenCookieResponseHelper refreshTokenCookieResponseHelper;
    @InjectMocks private TokenResponseEntityCreator tokenResponseEntityCreator;

    @Test
    void generate_shouldReturnResponseEntityWithAccessAndRefreshToken_whenUserIsValid() {
        var refreshCookie = ResponseCookie.from("test-refresh-cookie").build();
        var user = new User();
        var accessToken = "access-token";
        var refreshToken = UUID.randomUUID();
        var refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(refreshToken);
        when(jwtService.generateAccessToken(eq(user))).thenReturn(accessToken);
        when(refreshTokenService.generateRefreshToken(eq(user))).thenReturn(refreshTokenEntity);
        when(refreshTokenService.getExpirationSecond()).thenReturn(1L);
        when(refreshTokenCookieResponseHelper.createRefreshTokenCookie(eq(refreshToken), anyLong())).thenReturn(refreshCookie);
        var expectedBody = new LoginResponse(accessToken, refreshToken);

        var response = tokenResponseEntityCreator.generate(user);
        var body = response.getBody();

        assertNotNull(response);
        assertEquals(refreshCookie.toString(), response.getHeaders().getFirst(HttpHeaders.SET_COOKIE));
        assertEquals(expectedBody, body);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }
}
