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

@Service
@RequiredArgsConstructor
public final class TokenResponseEntityCreator {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenCookieResponseHelper refreshTokenCookieResponseHelper;

    public ResponseEntity<LoginResponse> generate(User user) {
        //TODO: separate logic for web (access - body, refresh - cookie) and mobile/desktop (both - body)
        var accessToken = jwtService.generateAccessToken(user);
        var refreshTokenEntity = refreshTokenService.generateRefreshToken(user);
        var refreshToken = refreshTokenEntity.getToken();

        var refreshTokenCookie = refreshTokenCookieResponseHelper.createRefreshTokenCookie(
                refreshToken,
                refreshTokenService.getExpirationSecond());

        var response = new LoginResponse(accessToken, refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(response);
    }
}
