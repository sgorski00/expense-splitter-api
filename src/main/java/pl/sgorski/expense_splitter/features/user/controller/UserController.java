package pl.sgorski.expense_splitter.features.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.expense_splitter.features.user.domain.Role;
import pl.sgorski.expense_splitter.features.user.dto.request.CreateUserRequest;
import pl.sgorski.expense_splitter.features.user.dto.response.DetailedUserResponse;
import pl.sgorski.expense_splitter.features.user.dto.response.UserResponse;

import java.awt.print.Pageable;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping(value = "/users", version = "1.0.0")
@Tag(name = "Users", description = "Endpoints for user management and administration.")
public final class UserController {

    @GetMapping
    @Operation(
            summary = "List all users",
            description = "Retrieves a paginated list of all users in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Users retrieved successfully."
            )
    })
    public ResponseEntity<Page<UserResponse>> getUsers(
            Pageable pageable
    ) {
        var result = new PageImpl<>(List.of(new UserResponse(UUID.randomUUID(), "user@example.com", Role.USER, Instant.now()))); //TODO: implement
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @Operation(
            summary = "Create new user",
            description = "Creates a new user account with the provided credentials and role."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User created successfully."
            )
    })
    public ResponseEntity<DetailedUserResponse> addUser(
            @RequestBody @Valid CreateUserRequest request
    ) {
        var path = URI.create("/path/to/new/user"); //TODO: implement
        return ResponseEntity.created(path).build();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get user by ID",
            description = "Retrieves detailed information about a specific user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User retrieved successfully."
            )
    })
    public ResponseEntity<DetailedUserResponse> getUser(
            @PathVariable Long id
    ) {
        var result = new DetailedUserResponse(UUID.randomUUID(), "user@example.com", "John", "Doe", Role.USER, Set.of(), Instant.now(), Instant.now(), null); //TODO: implement
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deactivate user",
            description = "Deactivates a user account, preventing login but preserving data."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "User deactivated successfully."
            )
    })
    public ResponseEntity<DetailedUserResponse> deactivateUser(
            @PathVariable Long id
    ) {
        //TODO: implement
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Update user details",
            description = "Updates user profile information and role assignment."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully."
            )
    })
    public ResponseEntity<DetailedUserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid CreateUserRequest request
    ) {
        var result = new DetailedUserResponse(UUID.randomUUID(), "user@example.com", "John", "Doe", Role.USER, Set.of(), Instant.now(), Instant.now(), null); //TODO: implement
        return ResponseEntity.ok(result);
    }
}
