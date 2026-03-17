package pl.sgorski.expense_splitter.features.friendship.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.expense_splitter.features.friendship.dto.request.FriendshipRequest;
import pl.sgorski.expense_splitter.features.friendship.dto.response.FriendshipResponse;
import pl.sgorski.expense_splitter.features.user.dto.response.UserResponse;

import java.net.URI;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping(path = "/friendships", version = "1")
public final class FriendshipController {

    @GetMapping
    public ResponseEntity<Page<FriendshipResponse>> getMyFriendships(
            Pageable pageable,
            Authentication authentication
    ) {
        // TODO: implement
        var requester = new UserResponse(1L, "user1@example.com", "USER", Instant.now());
        var recipient = new UserResponse(2L, "user2@example.com", "USER", Instant.now());
        var result = new PageImpl<>(List.of(new FriendshipResponse(1L, requester, recipient, "PENDING", Instant.now(), Instant.now(), null)));
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<FriendshipResponse> sendFriendRequest(
            @RequestBody @Valid FriendshipRequest request,
            Authentication authentication
    ) {
        var path = URI.create("/path/to/friendship"); // TODO: implement
        return ResponseEntity.created(path).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FriendshipResponse> getFriendship(
            @PathVariable Long id,
            Authentication authentication
    ) {
        // TODO: implement
        var requester = new UserResponse(1L, "user1@example.com", "USER", Instant.now());
        var recipient = new UserResponse(2L, "user2@example.com", "USER", Instant.now());
        var result = new FriendshipResponse(1L, requester, recipient, "PENDING", Instant.now(), Instant.now(), null);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<FriendshipResponse> acceptFriendship(
            @PathVariable Long id,
            Authentication authentication
    ) {
        // TODO: implement
        var requester = new UserResponse(1L, "user1@example.com", "USER", Instant.now());
        var recipient = new UserResponse(2L, "user2@example.com", "USER", Instant.now());
        var result = new FriendshipResponse(1L, requester, recipient, "PENDING", Instant.now(), Instant.now(), null);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<FriendshipResponse> rejectFriendship(
            @PathVariable Long id,
            Authentication authentication
    ) {
        // TODO: implement
        var requester = new UserResponse(1L, "user1@example.com", "USER", Instant.now());
        var recipient = new UserResponse(2L, "user2@example.com", "USER", Instant.now());
        var result = new FriendshipResponse(1L, requester, recipient, "PENDING", Instant.now(), Instant.now(), null);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> endFriendship(
            @PathVariable Long id,
            Authentication authentication
    ) {
        // TODO: implement
        return ResponseEntity.noContent().build();
    }
}
