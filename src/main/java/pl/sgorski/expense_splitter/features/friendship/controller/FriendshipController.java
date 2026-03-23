package pl.sgorski.expense_splitter.features.friendship.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.expense_splitter.features.friendship.dto.request.FriendshipRequest;
import pl.sgorski.expense_splitter.features.friendship.dto.response.FriendshipResponse;
import pl.sgorski.expense_splitter.features.user.domain.Role;
import pl.sgorski.expense_splitter.features.user.dto.response.UserResponse;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/friendships", version = "1.0.0")
@Tag(name = "Friendships", description = "Endpoints for friendship management and friend requests.")
public final class FriendshipController {

    @GetMapping
    @Operation(
            summary = "List my friendships",
            description = "Retrieves a paginated list of friendships for the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Friendships retrieved successfully."
            )
    })
    public ResponseEntity<Page<FriendshipResponse>> getMyFriendships(
            Pageable pageable,
            Authentication authentication
    ) {
        // TODO: implement
        var requester = new UserResponse(UUID.randomUUID(), "user1@example.com", Role.USER, Instant.now());
        var recipient = new UserResponse(UUID.randomUUID(), "user2@example.com", Role.USER, Instant.now());
        var result = new PageImpl<>(List.of(new FriendshipResponse(UUID.randomUUID(), requester, recipient, "PENDING", Instant.now(), Instant.now(), null)));
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @Operation(
            summary = "Send friend request",
            description = "Sends a friendship invitation to another user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Friend request sent successfully."
            )
    })
    public ResponseEntity<FriendshipResponse> sendFriendRequest(
            @RequestBody @Valid FriendshipRequest request,
            Authentication authentication
    ) {
        var path = URI.create("/path/to/friendship"); // TODO: implement
        return ResponseEntity.created(path).build();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get friendship details",
            description = "Retrieves detailed information about a specific friendship relation."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Friendship retrieved successfully."
            )
    })
    public ResponseEntity<FriendshipResponse> getFriendship(
            @PathVariable Long id,
            Authentication authentication
    ) {
        // TODO: implement
        var requester = new UserResponse(UUID.randomUUID(), "user1@example.com", Role.USER, Instant.now());
        var recipient = new UserResponse(UUID.randomUUID(), "user2@example.com", Role.USER, Instant.now());
        var result = new FriendshipResponse(UUID.randomUUID(), requester, recipient, "PENDING", Instant.now(), Instant.now(), null);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}/accept")
    @Operation(
            summary = "Accept friend request",
            description = "Accepts a pending friendship invitation."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Friend request accepted successfully."
            )
    })
    public ResponseEntity<FriendshipResponse> acceptFriendship(
            @PathVariable Long id,
            Authentication authentication
    ) {
        // TODO: implement
        var requester = new UserResponse(UUID.randomUUID(), "user1@example.com", Role.USER, Instant.now());
        var recipient = new UserResponse(UUID.randomUUID(), "user2@example.com", Role.USER, Instant.now());
        var result = new FriendshipResponse(UUID.randomUUID(), requester, recipient, "PENDING", Instant.now(), Instant.now(), null);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}/reject")
    @Operation(
            summary = "Reject friend request",
            description = "Rejects a pending friendship invitation."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Friend request rejected successfully."
            )
    })
    public ResponseEntity<FriendshipResponse> rejectFriendship(
            @PathVariable Long id,
            Authentication authentication
    ) {
        // TODO: implement
        var requester = new UserResponse(UUID.randomUUID(), "user1@example.com", Role.USER, Instant.now());
        var recipient = new UserResponse(UUID.randomUUID(), "user2@example.com", Role.USER, Instant.now());
        var result = new FriendshipResponse(UUID.randomUUID(), requester, recipient, "PENDING", Instant.now(), Instant.now(), null);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "End friendship",
            description = "Removes an existing friendship relation between two users."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Friendship ended successfully."
            )
    })
    public ResponseEntity<Void> endFriendship(
            @PathVariable Long id,
            Authentication authentication
    ) {
        // TODO: implement
        return ResponseEntity.noContent().build();
    }
}
