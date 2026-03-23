package pl.sgorski.expense_splitter.features.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.expense_splitter.features.auth.dto.request.LoginRequest;
import pl.sgorski.expense_splitter.features.auth.dto.request.RegisterRequest;
import pl.sgorski.expense_splitter.features.auth.dto.response.LoginResponse;
import pl.sgorski.expense_splitter.features.auth.local.service.LocalAuthService;
import pl.sgorski.expense_splitter.features.auth.mapper.AuthMapper;
import pl.sgorski.expense_splitter.features.auth.refresh_token.service.RefreshTokenCookieResponseHelper;
import pl.sgorski.expense_splitter.features.auth.refresh_token.service.RefreshTokenService;
import pl.sgorski.expense_splitter.features.user.dto.response.UserResponse;
import pl.sgorski.expense_splitter.features.user.mapper.UserMapper;
import pl.sgorski.expense_splitter.features.user.service.UserService;
import pl.sgorski.expense_splitter.security.authenticated.AuthenticatedUserResolver;
import pl.sgorski.expense_splitter.security.jwt.JwtService;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping(value = "/auth", version = "1.0.0")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration.")
@RequiredArgsConstructor
public final class AuthController {

    private final AuthenticatedUserResolver authenticatedUserResolver;
    private final LocalAuthService localAuthService;
    private final AuthMapper authMapper;
    private final UserMapper userMapper;
    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenCookieResponseHelper cookieResponseHelper;

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate user",
            description = "Authenticates a user with email and password, then returns access and refresh tokens."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User authenticated successfully."
            )
    })
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        var user = localAuthService.login(authMapper.toCommand(request));
        var jwtToken = new LoginResponse(jwtService.generateAccessToken(user));
        var refreshToken = refreshTokenService.generateRefreshToken(user);
        var cookie = cookieResponseHelper.createRefreshTokenCookie(
                refreshToken.getToken(),
                refreshTokenService.getExpirationSecond());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(jwtToken);
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register new user",
            description = "Creates a new user account and returns the created user resource."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully."
            )
    })
    public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterRequest request) {
        var command = authMapper.toCommand(request);
        var user = localAuthService.registerUser(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userMapper.toResponse(user));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(
            @CookieValue(RefreshTokenCookieResponseHelper.REFRESH_TOKEN_COOKIE_KEY) UUID refreshTokenCookie,
            Authentication authentication
    ) {
        var userId = authenticatedUserResolver.requireUserId(authentication);
        var user = userService.getUser(userId);
        refreshTokenService.validateToken(refreshTokenCookie, user);
        refreshTokenService.revokeToken(refreshTokenCookie);
        var refreshToken = refreshTokenService.generateRefreshToken(user);
        var cookie = cookieResponseHelper.createRefreshTokenCookie(
                refreshToken.getToken(),
                refreshTokenService.getExpirationSecond());
        var jwtTokenStr = jwtService.generateAccessToken(user);
        var jwtToken = new LoginResponse(jwtTokenStr);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,  cookie.toString())
                .body(jwtToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(value = RefreshTokenCookieResponseHelper.REFRESH_TOKEN_COOKIE_KEY, required = false) UUID refreshTokenCookie
    ) {
        var cookie = cookieResponseHelper.createClearRefreshTokenCookie();
        refreshTokenService.revokeToken(refreshTokenCookie);
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}
