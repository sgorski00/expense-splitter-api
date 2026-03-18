package pl.sgorski.expense_splitter.features.user.controller;

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
public final class ProfileController {

    @GetMapping
    public ResponseEntity<DetailedUserResponse> getMyProfile(
            Authentication authentication
    ) {
        var result = new DetailedUserResponse(1L, "user@example.com", "John", "Doe", "USER", Instant.now(), Instant.now(), null); // TODO: implement
        return ResponseEntity.ok(result);
    }

    @PatchMapping
    public ResponseEntity<DetailedUserResponse> updateProfile(
            @RequestBody @Valid UpdateUserRequest request,
            Authentication authentication
    ) {
        var result = new DetailedUserResponse(1L, "user@example.com", "John", "Doe", "USER", Instant.now(), Instant.now(), null); // TODO: implement
        return ResponseEntity.ok(result);
    }

    @DeleteMapping
    public ResponseEntity<DetailedUserResponse> deactivateAccount(
            Authentication authentication
    ) {
        // TODO: implement
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
            @RequestBody @Valid PasswordChangeRequest request,
            Authentication authentication
    ) {
        // TODO: implement
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/link/{provider}")
    public ResponseEntity<String> linkOAuth2Account(
            @PathVariable String provider, //TODO: auth proivder enum
            Authentication authentication
    ) {
        var redirectPath = URI.create("/path/to/oauth2/link"); // TODO: implement
        return ResponseEntity.status(HttpStatus.FOUND).location(redirectPath).build();
    }

    @GetMapping("/friends")
    public ResponseEntity<Page<UserResponse>> getMyFriends(
            Authentication authentication
    ) {
        var result = new PageImpl<>(List.of(new UserResponse(2L, "user@example.com", "User", Instant.now()))); // TODO: implement
        return ResponseEntity.ok(result);
    }
}
