package pl.sgorski.expense_splitter.features.friendship.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.expense_splitter.features.friendship.domain.FriendshipStatus;
import pl.sgorski.expense_splitter.features.friendship.dto.request.FriendshipRequest;
import pl.sgorski.expense_splitter.features.friendship.dto.response.FriendshipResponse;
import pl.sgorski.expense_splitter.features.friendship.mapper.FriendshipMapper;
import pl.sgorski.expense_splitter.features.friendship.service.FriendshipService;
import pl.sgorski.expense_splitter.security.authenticated.AuthenticatedUserResolver;

import java.util.UUID;

@RestController
@RequestMapping(path = "/friendships", version = "1.0.0")
@Tag(name = "Friendships", description = "Endpoints for friendship management and friend requests.")
@RequiredArgsConstructor
public final class FriendshipController {

    private final FriendshipMapper friendshipMapper;
    private final FriendshipService friendshipService;
    private final AuthenticatedUserResolver authenticatedUserResolver;

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
            @RequestParam FriendshipStatus status,
            Authentication authentication
    ) {
        var user = authenticatedUserResolver.requireUser(authentication);
        var friendships = friendshipService.getFriendshipsByStatus(user, status, pageable)
                .map(friendshipMapper::toResponse);
        return ResponseEntity.ok(friendships);
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
        var user = authenticatedUserResolver.requireUser(authentication);
        var command = friendshipMapper.toCommand(request);
        var friendship = friendshipService.createFriendshipRequest(user, command);
        var response = friendshipMapper.toResponse(friendship);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
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
            @PathVariable UUID id,
            Authentication authentication
    ) {
        var user = authenticatedUserResolver.requireUser(authentication);
        var friendship = friendshipService.getFriendshipForUser(id, user);
        var response = friendshipMapper.toResponse(friendship);
        return ResponseEntity.ok(response);
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
            @PathVariable UUID id,
            Authentication authentication
    ) {
        var user = authenticatedUserResolver.requireUser(authentication);
        var friendship = friendshipService.updateFriendshipStatus(id, user, FriendshipStatus.ACCEPTED);
        var result = friendshipMapper.toResponse(friendship);
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
            @PathVariable UUID id,
            Authentication authentication
    ) {
        var user = authenticatedUserResolver.requireUser(authentication);
        var friendship = friendshipService.updateFriendshipStatus(id, user, FriendshipStatus.REJECTED);
        var result = friendshipMapper.toResponse(friendship);
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
            @PathVariable UUID id,
            Authentication authentication
    ) {
        var user = authenticatedUserResolver.requireUser(authentication);
        friendshipService.deleteFriendship(id, user);
        return ResponseEntity.noContent().build();
    }
}
