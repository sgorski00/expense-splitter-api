package pl.sgorski.expense_splitter.features.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.expense_splitter.features.auth.dto.request.*;
import pl.sgorski.expense_splitter.features.auth.dto.response.LoginResponse;
import pl.sgorski.expense_splitter.features.auth.local.service.LocalAuthService;
import pl.sgorski.expense_splitter.features.auth.local.utils.TokenResponseEntityCreator;
import pl.sgorski.expense_splitter.features.auth.mapper.AuthMapper;
import pl.sgorski.expense_splitter.features.auth.refresh_token.service.RefreshTokenCookieResponseHelper;
import pl.sgorski.expense_splitter.features.auth.refresh_token.service.RefreshTokenExtractor;
import pl.sgorski.expense_splitter.features.auth.refresh_token.service.RefreshTokenService;
import pl.sgorski.expense_splitter.features.auth.two_fa.service.UserTwoFactorService;
import pl.sgorski.expense_splitter.features.user.dto.response.UserResponse;
import pl.sgorski.expense_splitter.features.user.mapper.UserMapper;
import pl.sgorski.expense_splitter.security.authenticated.AuthenticatedUserResolver;

@RestController
@RequestMapping(value = "/auth", version = "1.0.0")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration.")
@RequiredArgsConstructor
public final class AuthController {

  private final LocalAuthService localAuthService;
  private final AuthMapper authMapper;
  private final UserMapper userMapper;
  private final RefreshTokenExtractor refreshTokenExtractor;
  private final RefreshTokenService refreshTokenService;
  private final RefreshTokenCookieResponseHelper refreshTokenCookieResponseHelper;
  private final TokenResponseEntityCreator tokensResponseEntityCreator;
  private final UserTwoFactorService userTwoFactorService;
  private final AuthenticatedUserResolver authenticatedUserResolver;

  @PostMapping("/login")
  @Operation(
      summary = "Authenticate user",
      description =
          """
                    Authenticates a user with email and password. Access and refresh tokens are issued in secure httpOnly cookies.<br><br>
                    If user's password is marked to be changed, the access token will be generated but will allow only to access these endpoints:
                    - /profile/password (PUT and PATCH for password change/set)<br>
                    - /auth/logout (allow users to logout)<br>
                    - /auth/refresh (allow refresh token)<br><br>
                    If user has 2FA enabled, the access token will be generated with limited permissions and the response will indicate that 2FA verification is required.
                    User must then verify 2FA code using /auth/2fa/verify endpoint to complete authentication and receive a new access token with full permissions.
                    """)
  @ApiResponse(
      responseCode = "200",
      description =
          "User authenticated successfully. Access and refresh tokens issued in secure httpOnly cookies and in response body.")
  public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
    var user = localAuthService.login(authMapper.toCommand(request));
    return tokensResponseEntityCreator.generate(user, user.isTwoFactorRequired());
  }

  @PostMapping("/2fa/verify")
  @Operation(
      summary = "Verify 2FA code",
      description =
          "Verifies TOTP code from Google Authenticator. Completes authentication and issues final JWT with full access.")
  @ApiResponse(responseCode = "200", description = "2FA verified successfully.")
  public ResponseEntity<LoginResponse> verify2FA(
      @RequestBody @Valid GoogleAuthenticatorRequest request, Authentication authentication) {
    var user = authenticatedUserResolver.requireUser(authentication);
    userTwoFactorService.verify2FA(user.getId(), request);
    return tokensResponseEntityCreator.generate(user, false);
  }

  @PostMapping("/register")
  @Operation(
      summary = "Register new user",
      description = "Creates a new user account and returns the created user resource.")
  @ApiResponse(responseCode = "201", description = "User registered successfully.")
  public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterRequest request) {
    var command = authMapper.toCommand(request);
    var user = localAuthService.registerUser(command);
    return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toResponse(user));
  }

  @PostMapping("/refresh")
  @Operation(
      summary = "Refresh access token",
      description =
          "Generates a new access token using a valid refresh token. Supports both cookie (web) and Authorization header (mobile/desktop) methods.")
  @ApiResponse(
      responseCode = "201",
      description =
          "Access token refreshed successfully. New tokens issued in secure httpOnly cookies and in response body.")
  public ResponseEntity<LoginResponse> refreshToken(
      @Nullable
          @CookieValue(
              value = RefreshTokenCookieResponseHelper.REFRESH_TOKEN_COOKIE_KEY,
              required = false)
          UUID refreshTokenCookieValue,
      HttpServletRequest request) {
    var refreshTokenValue = refreshTokenExtractor.extract(refreshTokenCookieValue, request);
    var existingRefreshToken = refreshTokenService.getValidToken(refreshTokenValue);
    refreshTokenService.revokeToken(refreshTokenValue);
    return tokensResponseEntityCreator.generate(existingRefreshToken.getUser(), false);
  }

  @PostMapping("/logout")
  @Operation(
      summary = "Revoke refresh token",
      description = "Revokes a refresh token from the cookie, effectively logging the user out.")
  @ApiResponse(responseCode = "204", description = "Refresh token revoked successfully.")
  public ResponseEntity<Void> logout(
      @Nullable
          @CookieValue(
              value = RefreshTokenCookieResponseHelper.REFRESH_TOKEN_COOKIE_KEY,
              required = false)
          UUID refreshTokenCookie) {
    var refreshTokenClearCookie = refreshTokenCookieResponseHelper.createClearRefreshTokenCookie();
    if (refreshTokenCookie != null) refreshTokenService.revokeToken(refreshTokenCookie);
    return ResponseEntity.noContent()
        .header(HttpHeaders.SET_COOKIE, refreshTokenClearCookie.toString())
        .build();
  }

  @PostMapping("/reset-password")
  @Operation(
      summary = "Reset user password",
      description =
          "Generates a user's password reset token and sends an email to the user with instructions to set a new password.")
  @ApiResponse(responseCode = "204", description = "Password reset token generated successfully.")
  public ResponseEntity<Void> resetPassword(@RequestBody @Valid PasswordResetRequest request) {
    localAuthService.requestPasswordReset(request.email());
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/reset-password/confirm")
  @Operation(
      summary = "Confirm reset user password",
      description = "Reset user's password using previously generated password reset token.")
  @ApiResponse(responseCode = "204", description = "Password reset successfully.")
  public ResponseEntity<Void> confirmResetPassword(
      @RequestBody @Valid ConfirmPasswordResetRequest request) {
    var command = authMapper.toConfirmPasswordResetCommand(request);
    localAuthService.resetPassword(command);
    return ResponseEntity.noContent().build();
  }
}
