package pl.sgorski.expense_splitter.features.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.expense_splitter.features.user.dto.request.PasswordChangeRequest;
import pl.sgorski.expense_splitter.features.user.dto.request.UpdateUserRequest;
import pl.sgorski.expense_splitter.features.user.dto.response.DetailedUserResponse;
import pl.sgorski.expense_splitter.features.user.dto.response.UserResponse;

import java.net.URI;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping(value = "/profile", version = "1.0.0")
@Tag(name = "Profile", description = "Endpoints for user profile management and personal account operations.")
public final class ProfileController {

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
        var result = new DetailedUserResponse(1L, "user@example.com", "John", "Doe", "USER", Instant.now(), Instant.now(), null); // TODO: implement
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
        var result = new DetailedUserResponse(1L, "user@example.com", "John", "Doe", "USER", Instant.now(), Instant.now(), null); // TODO: implement
        return ResponseEntity.ok(result);
    }

    @DeleteMapping
    @Operation(
            summary = "Deactivate my account",
            description = "Deactivates the authenticated user's account, preventing further login."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Account deactivated successfully."
            )
    })
    public ResponseEntity<DetailedUserResponse> deactivateAccount(
            Authentication authentication
    ) {
        // TODO: implement
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/password")
    @Operation(
            summary = "Change password",
            description = "Changes the authenticated user's account password."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Password changed successfully."
            )
    })
    public ResponseEntity<Void> changePassword(
            @RequestBody @Valid PasswordChangeRequest request,
            Authentication authentication
    ) {
        // TODO: implement
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/link/{provider}")
    @Operation(
            summary = "Link OAuth2 account",
            description = "Initiates OAuth2 account linking with the specified provider."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "302",
                    description = "Redirecting to OAuth2 provider login."
            )
    })
    public ResponseEntity<String> linkOAuth2Account(
            @PathVariable String provider, //TODO: auth proivder enum
            Authentication authentication
    ) {
        var redirectPath = URI.create("/path/to/oauth2/link"); // TODO: implement
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
        var result = new PageImpl<>(List.of(new UserResponse(2L, "user@example.com", "User", Instant.now()))); // TODO: implement
        return ResponseEntity.ok(result);
    }
}
