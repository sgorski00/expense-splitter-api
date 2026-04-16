package pl.sgorski.expense_splitter.features.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.expense_splitter.features.auth.local.service.LocalAuthService;
import pl.sgorski.expense_splitter.features.user.domain.Role;
import pl.sgorski.expense_splitter.features.user.dto.request.CreateUserRequest;
import pl.sgorski.expense_splitter.features.user.dto.request.PasswordSetRequest;
import pl.sgorski.expense_splitter.features.user.dto.request.UpdateUserRequest;
import pl.sgorski.expense_splitter.features.user.dto.response.DetailedUserResponse;
import pl.sgorski.expense_splitter.features.user.dto.response.UserResponse;
import pl.sgorski.expense_splitter.features.user.mapper.UserMapper;
import pl.sgorski.expense_splitter.features.user.service.UserService;

@RestController
@RequestMapping(value = "/admin/users", version = "1.0.0")
@Tag(name = "Users - Admin", description = "Endpoints for user management and administration.")
@RequiredArgsConstructor
public final class AdminUserController {

  private final UserService userService;
  private final LocalAuthService authService;
  private final UserMapper userMapper;

  @GetMapping
  @Operation(
      summary = "List all users",
      description = "Retrieves a paginated list of all users in the system.")
  @ApiResponse(responseCode = "200", description = "Users retrieved successfully.")
  public ResponseEntity<Page<UserResponse>> getUsers(
      @RequestParam(name = "query", required = false) String query,
      @RequestParam(name = "role", required = false) Role role,
      Pageable pageable) {
    var response = userService.searchUsersAdmin(query, role, pageable).map(userMapper::toResponse);
    return ResponseEntity.ok(response);
  }

  @PostMapping
  @Operation(
      summary = "Create new user",
      description = "Creates a new user account with the provided credentials and role.")
  @ApiResponse(responseCode = "201", description = "User created successfully.")
  public ResponseEntity<DetailedUserResponse> addUser(
      @RequestBody @Valid CreateUserRequest request) {
    var command = userMapper.toCreateCommand(request);
    var user = userService.createUser(command);
    var response = userMapper.toDetailedResponse(user);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Get user by ID",
      description = "Retrieves detailed information about a specific user.")
  @ApiResponse(responseCode = "200", description = "User retrieved successfully.")
  public ResponseEntity<DetailedUserResponse> getUser(@PathVariable UUID id) {
    var result = userService.getUser(id);
    var response = userMapper.toDetailedResponse(result);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  @Operation(
      summary = "Soft delete user",
      description = "Deletes a user account, preventing login but preserving data.")
  @ApiResponse(responseCode = "204", description = "User deactivated successfully.")
  public ResponseEntity<Void> deactivateUser(@PathVariable UUID id) {
    var user = userService.getUser(id);
    userService.deleteUser(user);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}")
  @Operation(
      summary = "Update user details",
      description = "Updates user profile information and role assignment.")
  @ApiResponse(responseCode = "200", description = "User updated successfully.")
  public ResponseEntity<DetailedUserResponse> updateUser(
      @PathVariable UUID id, @RequestBody @Valid UpdateUserRequest request) {
    var command = userMapper.toUpdateCommand(request);
    var result = userService.updateUser(id, command);
    var response = userMapper.toDetailedResponse(result);
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{id}/password")
  @Operation(summary = "Change user password", description = "Changes user password.")
  @ApiResponse(responseCode = "200", description = "User updated successfully.")
  public ResponseEntity<DetailedUserResponse> changePassword(
      @PathVariable UUID id, @RequestBody @Valid PasswordSetRequest request) {
    var result = userService.changePassword(id, request.newPassword());
    var response = userMapper.toDetailedResponse(result);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{id}/reset-password")
  @Operation(
      summary = "Request user password reset",
      description =
          "Sends request with user password reset. User will receive email with instructions to reset password.")
  @ApiResponse(
      responseCode = "204",
      description = "User password reset request generated successfully.")
  public ResponseEntity<Void> requestResetPassword(@PathVariable UUID id) {
    authService.requestPasswordResetAndThrowsWhenUserNotFound(id);
    return ResponseEntity.noContent().build();
  }
}
