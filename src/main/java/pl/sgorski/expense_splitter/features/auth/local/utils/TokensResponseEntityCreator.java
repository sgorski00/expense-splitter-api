package pl.sgorski.expense_splitter.features.auth.local.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.features.auth.dto.response.LoginResponse;
import pl.sgorski.expense_splitter.features.auth.refresh_token.service.RefreshTokenCookieResponseHelper;
import pl.sgorski.expense_splitter.features.auth.refresh_token.service.RefreshTokenService;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.security.jwt.JwtService;
import pl.sgorski.expense_splitter.security.oauth2.AccessTokenCookieResponseHelper;

@Service
@RequiredArgsConstructor
public final class TokensResponseEntityCreator {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenCookieResponseHelper refreshTokenCookieResponseHelper;
    private final AccessTokenCookieResponseHelper accessTokenCookieResponseHelper;
    /**
     * Helper method to generate new access and refresh tokens after password operation.
     * Revokes all existing refresh tokens and returns a new token pair in cookies and response body.
     */
    public ResponseEntity<LoginResponse> generate(User user) {
        var accessToken = jwtService.generateAccessToken(user);
        var refreshTokenEntity = refreshTokenService.generateRefreshToken(user);

        var accessTokenCookie = accessTokenCookieResponseHelper.createAccessTokenCookie(
                accessToken,
                jwtService.getExpirationSecond());
        var refreshTokenCookie = refreshTokenCookieResponseHelper.createRefreshTokenCookie(
                refreshTokenEntity.getToken(),
                refreshTokenService.getExpirationSecond());

        var response = new LoginResponse(accessToken, refreshTokenEntity.getToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(response);
    }
}
