package pl.sgorski.expense_splitter.features.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.sgorski.expense_splitter.features.auth.dto.request.GoogleAuthenticatorRequest;
import pl.sgorski.expense_splitter.features.auth.dto.response.LoginResponse;
import pl.sgorski.expense_splitter.features.auth.local.service.LocalAuthService;
import pl.sgorski.expense_splitter.features.auth.local.utils.TokenResponseEntityCreator;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;
import pl.sgorski.expense_splitter.features.auth.refresh_token.service.RefreshTokenService;
import pl.sgorski.expense_splitter.features.auth.two_fa.service.UserTwoFactorService;
import pl.sgorski.expense_splitter.features.expense.dto.filter.ExpenseRole;
import pl.sgorski.expense_splitter.features.friendship.service.FriendshipService;
import pl.sgorski.expense_splitter.features.payment.dto.response.PaymentResponse;
import pl.sgorski.expense_splitter.features.payment.mapper.PaymentMapper;
import pl.sgorski.expense_splitter.features.payment.service.PaymentService;
import pl.sgorski.expense_splitter.features.user.dto.request.PasswordChangeRequest;
import pl.sgorski.expense_splitter.features.user.dto.request.PasswordSetRequest;
import pl.sgorski.expense_splitter.features.user.dto.request.UpdateProfileRequest;
import pl.sgorski.expense_splitter.features.user.dto.response.DetailedUserResponse;
import pl.sgorski.expense_splitter.features.user.dto.response.UserResponse;
import pl.sgorski.expense_splitter.features.user.mapper.UserMapper;
import pl.sgorski.expense_splitter.features.user.service.UserService;
import pl.sgorski.expense_splitter.security.authenticated.AuthenticatedUserResolver;
import pl.sgorski.expense_splitter.security.oauth2.session.OAuth2SessionService;

@RestController
@RequestMapping(value = "/profile", version = "1.0.0")
@Tag(
    name = "Profile",
    description = "Endpoints for user profile management and personal account operations.")
@RequiredArgsConstructor
@Slf4j
public final class ProfileController {

  private final AuthenticatedUserResolver authenticatedUserResolver;
  private final UserService userService;
  private final UserMapper userMapper;
  private final LocalAuthService localAuthService;
  private final RefreshTokenService refreshTokenService;
  private final TokenResponseEntityCreator tokensResponseEntityCreator;
  private final FriendshipService friendshipService;
  private final PaymentService paymentService;
  private final PaymentMapper paymentMapper;
  private final UserTwoFactorService userTwoFactorService;

  @GetMapping
  @Operation(
      summary = "Get my profile",
      description = "Retrieves the authenticated user's profile information.")
  @ApiResponse(responseCode = "200", description = "Profile retrieved successfully.")
  public ResponseEntity<DetailedUserResponse> getMyProfile(Authentication authentication) {
    var user = authenticatedUserResolver.requireUser(authentication);
    var result = userMapper.toDetailedResponse(user);
    return ResponseEntity.ok(result);
  }

  @PatchMapping
  @Operation(
      summary = "Update my profile",
      description =
          "Updates the authenticated user's profile information (email, first name, last name).")
  @ApiResponse(responseCode = "200", description = "Profile updated successfully.")
  public ResponseEntity<DetailedUserResponse> updateProfile(
      @RequestBody @Valid UpdateProfileRequest request, Authentication authentication) {
    var user = authenticatedUserResolver.requireUser(authentication);
    userMapper.updateProfile(request, user);
    user = userService.save(user);
    var result = userMapper.toDetailedResponse(user);
    return ResponseEntity.ok(result);
  }

  @DeleteMapping
  @Operation(
      summary = "Delete my account",
      description = "Deletes the authenticated user's account, preventing further login.")
  @ApiResponse(responseCode = "204", description = "Account deleted successfully.")
  public ResponseEntity<Void> deleteAccount(Authentication authentication) {
    var user = authenticatedUserResolver.requireUser(authentication);
    userService.deleteUser(user);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/password")
  @Operation(
      summary = "Set local password (applicable only if the account was created via OAuth2)",
      description =
          "Setting the authenticated user's account password. New access and refresh tokens are issued in cookies and in response body.")
  @ApiResponse(
      responseCode = "200",
      description =
          "Password set successfully. Access and refresh tokens issued in secure httpOnly cookies.")
  public ResponseEntity<LoginResponse> setLocalPassword(
      @RequestBody @Valid PasswordSetRequest request, Authentication authentication) {
    var user = authenticatedUserResolver.requireUser(authentication);
    localAuthService.setLocalPassword(user, request.newPassword());
    refreshTokenService.revokeAllUserTokens(user.getId());
    return tokensResponseEntityCreator.generate(user, false);
  }

  @PatchMapping("/password")
  @Operation(
      summary = "Change password",
      description =
          "Changes the authenticated user's account password. New access and refresh tokens are issued in cookies and in response body.")
  @ApiResponse(
      responseCode = "200",
      description =
          "Password changed successfully. Tokens issued in secure httpOnly cookies and in response body.")
  public ResponseEntity<LoginResponse> changePassword(
      @RequestBody @Valid PasswordChangeRequest request, Authentication authentication) {
    var user = authenticatedUserResolver.requireUser(authentication);
    localAuthService.changePassword(user, request.oldPassword(), request.newPassword());
    refreshTokenService.revokeAllUserTokens(user.getId());
    return tokensResponseEntityCreator.generate(user, false);
  }

  @GetMapping("/link/{provider}")
  @Operation(
      summary = "Link OAuth2 account",
      description = "Initiates OAuth2 account linking with the specified provider")
  @ApiResponse(responseCode = "302", description = "Redirecting to OAuth2 provider login.")
  public ResponseEntity<Void> linkOAuth2Account(
      @PathVariable AuthProvider provider,
      HttpServletRequest request,
      Authentication authentication) {
    var userId = authenticatedUserResolver.requireUserId(authentication);
    var session = request.getSession(true);
    session.setAttribute(OAuth2SessionService.OAUTH_MODE_KEY, "link");
    session.setAttribute(OAuth2SessionService.OAUTH_LINK_USER_ID_KEY, userId);
    var redirectPath =
        ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/oauth2/authorization/")
            .path(provider.name().toLowerCase(Locale.ROOT))
            .build()
            .toUri();
    log.debug("Redirecting to OAuth2 authorization endpoint: {}", redirectPath);
    return ResponseEntity.status(HttpStatus.FOUND).location(redirectPath).build();
  }

  @GetMapping("/friends")
  @Operation(
      summary = "List my friends",
      description = "Retrieves a paginated list of the authenticated user's friends.")
  @ApiResponse(responseCode = "200", description = "Friends list retrieved successfully.")
  public ResponseEntity<Page<UserResponse>> getMyFriends(
      Pageable pageable, Authentication authentication) {
    var user = authenticatedUserResolver.requireUser(authentication);
    var result = friendshipService.getFriends(user, pageable).map(userMapper::toResponse);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/payments")
  @Operation(
      summary = "List my payments",
      description = "Retrieves a paginated list of the authenticated user's payments.")
  @ApiResponse(responseCode = "200", description = "Payments list retrieved successfully.")
  public ResponseEntity<Page<PaymentResponse>> getMyPayments(
      Pageable pageable, Authentication authentication) {
    var user = authenticatedUserResolver.requireUser(authentication);
    var result =
        paymentService
            .getPaymentsForUser(user, pageable)
            .map(
                payment ->
                    paymentMapper.toResponse(
                        payment, ExpenseRole.fromExpense(user, payment.getExpense())));
    return ResponseEntity.ok(result);
  }

  @PostMapping("/2fa/enable")
  @Operation(
      summary = "Initialize 2FA setup",
      description = "Generates secret and returns QR code for Google Authenticator setup.")
  @ApiResponse(
      responseCode = "201",
      description = "2FA first step completed successfully, QR code returned.")
  public ResponseEntity<byte[]> enableTwoFactor(Authentication authentication) {
    var userId = authenticatedUserResolver.requireUserId(authentication);
    var qrCodeBytes = userTwoFactorService.setup2FA(userId);
    return ResponseEntity.status(HttpStatus.CREATED)
        .header("Content-Type", MediaType.IMAGE_PNG_VALUE)
        .body(qrCodeBytes);
  }

  @PostMapping("/2fa/confirm")
  @Operation(
      summary = "Confirm 2FA setup",
      description = "Verifies OTP code and activates two-factor authentication.")
  @ApiResponse(
      responseCode = "204",
      description = "2FA second step completed successfully, 2FA enabled.")
  public ResponseEntity<Void> confirmTwoFactor(
      @RequestBody GoogleAuthenticatorRequest request, Authentication authentication) {
    var userId = authenticatedUserResolver.requireUserId(authentication);
    userTwoFactorService.confirm2FA(userId, request.code());
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/2fa/disable")
  @Operation(
      summary = "Disable 2FA",
      description = "Disables two-factor authentication for the user.")
  @ApiResponse(responseCode = "204", description = "2FA disabled successfully.")
  public ResponseEntity<Void> disableTwoFactor(Authentication authentication) {
    var userId = authenticatedUserResolver.requireUserId(authentication);
    userTwoFactorService.disable2FA(userId);
    return ResponseEntity.noContent().build();
  }
}
