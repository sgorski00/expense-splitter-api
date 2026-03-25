package pl.sgorski.expense_splitter.features.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.sgorski.expense_splitter.features.auth.dto.response.LoginResponse;
import pl.sgorski.expense_splitter.features.auth.local.utils.TokensResponseEntityCreator;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;
import pl.sgorski.expense_splitter.features.auth.local.service.LocalAuthService;
import pl.sgorski.expense_splitter.features.auth.refresh_token.service.RefreshTokenService;
import pl.sgorski.expense_splitter.features.user.domain.Role;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.dto.request.PasswordChangeRequest;
import pl.sgorski.expense_splitter.features.user.dto.request.PasswordSetRequest;
import pl.sgorski.expense_splitter.features.user.dto.request.UpdateUserRequest;
import pl.sgorski.expense_splitter.features.user.dto.response.DetailedUserResponse;
import pl.sgorski.expense_splitter.features.user.dto.response.UserResponse;
import pl.sgorski.expense_splitter.features.user.mapper.UserMapper;
import pl.sgorski.expense_splitter.features.user.service.UserService;
import pl.sgorski.expense_splitter.security.authenticated.AuthenticatedUserResolver;
import pl.sgorski.expense_splitter.security.oauth2.session.OAuth2SessionService;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping(value = "/profile", version = "1.0.0")
@Tag(name = "Profile", description = "Endpoints for user profile management and personal account operations.")
@RequiredArgsConstructor
@Slf4j
public final class ProfileController {

    private final AuthenticatedUserResolver authenticatedUserResolver;
    private final UserService userService;
    private final UserMapper userMapper;
    private final LocalAuthService localAuthService;
    private final RefreshTokenService refreshTokenService;
    private final TokensResponseEntityCreator tokensResponseEntityCreator;

    @GetMapping
    @Operation(
            summary = "Get my profile",
            description = "Retrieves the authenticated user's profile information."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile retrieved successfully."
            )
    })
    public ResponseEntity<DetailedUserResponse> getMyProfile(
            Authentication authentication
    ) {
        var user = fetchUser(authentication);
        var result = userMapper.toDetailedResponse(user);
        return ResponseEntity.ok(result);
    }

    @PatchMapping
    @Operation(
            summary = "Update my profile",
            description = "Updates the authenticated user's profile information (email, first name, last name)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile updated successfully."
            )
    })
    public ResponseEntity<DetailedUserResponse> updateProfile(
            @RequestBody @Valid UpdateUserRequest request,
            Authentication authentication
    ) {
        var user = fetchUser(authentication);
        userMapper.updateUser(request, user);
        user = userService.save(user);
        var result = userMapper.toDetailedResponse(user);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping
    @Operation(
            summary = "Delete my account",
            description = "Deletes the authenticated user's account, preventing further login."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Account deleted successfully."
            )
    })
    public ResponseEntity<Void> deleteAccount(
            Authentication authentication
    ) {
        var user = fetchUser(authentication);
        userService.deleteUser(user);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/password")
    @Operation(
            summary = "Set local password (applicable only if the account was created via OAuth2)",
            description = "Setting the authenticated user's account password. New access and refresh tokens are issued in cookies and in response body."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password set successfully. Access and refresh tokens issued in secure httpOnly cookies."
            )
    })
    public ResponseEntity<LoginResponse> setLocalPassword(
            @RequestBody @Valid PasswordSetRequest request,
            Authentication authentication
    ) {
        var user = fetchUser(authentication);
        localAuthService.setLocalPassword(user, request.newPassword());
        refreshTokenService.revokeAllUserTokens(user.getId());
        return tokensResponseEntityCreator.generate(user);
    }

    @PatchMapping("/password")
    @Operation(
            summary = "Change password",
            description = "Changes the authenticated user's account password. New access and refresh tokens are issued in cookies and in response body."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password changed successfully. Tokens issued in secure httpOnly cookies and in response body."
            )
    })
    public ResponseEntity<LoginResponse> changePassword(
            @RequestBody @Valid PasswordChangeRequest request,
            Authentication authentication
    ) {
        var user = fetchUser(authentication);
        localAuthService.changePassword(user, request.oldPassword(), request.newPassword());
        refreshTokenService.revokeAllUserTokens(user.getId());
        return tokensResponseEntityCreator.generate(user);
    }

    @GetMapping("/link/{provider}")
    @Operation(
            summary = "Link OAuth2 account",
            description = "Initiates OAuth2 account linking with the specified provider"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "302",
                    description = "Redirecting to OAuth2 provider login."
            )
    })
    public ResponseEntity<Void> linkOAuth2Account(
            @PathVariable AuthProvider provider,
            HttpServletRequest request,
            Authentication authentication
    ) {
        var userId = authenticatedUserResolver.requireUserId(authentication);
        var session = request.getSession(true);
        session.setAttribute(OAuth2SessionService.OAUTH_MODE_KEY, "link");
        session.setAttribute(OAuth2SessionService.OAUTH_LINK_USER_ID_KEY, userId);
        var redirectPath = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/oauth2/authorization/")
                .path(provider.name().toLowerCase(Locale.ROOT))
                .build().toUri();
        log.debug("Redirecting to OAuth2 authorization endpoint: {}", redirectPath);
        return ResponseEntity.status(HttpStatus.FOUND).location(redirectPath).build();
    }

    @GetMapping("/friends")
    @Operation(
            summary = "List my friends",
            description = "Retrieves a paginated list of the authenticated user's friends."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Friends list retrieved successfully."
            )
    })
    public ResponseEntity<Page<UserResponse>> getMyFriends(
            Authentication authentication
    ) {
        var user = fetchUser(authentication);
        var result = new PageImpl<>(List.of(new UserResponse(UUID.randomUUID(), "user@example.com", Role.USER, Instant.now()))); // TODO: implement
        return ResponseEntity.ok(result);
    }

    private User fetchUser(Authentication authentication) {
        var userId = authenticatedUserResolver.requireUserId(authentication);
        return userService.getUser(userId);
    }
}
