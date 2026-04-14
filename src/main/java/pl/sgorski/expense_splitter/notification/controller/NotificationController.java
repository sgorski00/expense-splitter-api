package pl.sgorski.expense_splitter.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.expense_splitter.notification.dto.request.UpdateNotificationPreferenceRequest;
import pl.sgorski.expense_splitter.notification.dto.response.NotificationPreferenceResponse;
import pl.sgorski.expense_splitter.notification.dto.response.NotificationResponse;
import pl.sgorski.expense_splitter.notification.mapper.NotificationMapper;
import pl.sgorski.expense_splitter.notification.service.NotificationPreferenceService;
import pl.sgorski.expense_splitter.notification.service.NotificationService;
import pl.sgorski.expense_splitter.security.authenticated.AuthenticatedUserResolver;

@RestController
@RequestMapping(value = "/notifications", version = "1.0.0")
@Tag(name = "Notifications", description = "Endpoints for notification management and tracking.")
@RequiredArgsConstructor
public class NotificationController {

  private final AuthenticatedUserResolver authenticatedUserResolver;
  private final NotificationService notificationService;
  private final NotificationPreferenceService preferenceService;
  private final NotificationMapper mapper;

  @PatchMapping("{id}/read")
  @Operation(
      summary = "Mark notification as read",
      description = "Marks a specific notification as read for the authenticated user.")
  @ApiResponse(responseCode = "200", description = "Notification marked as read successfully.")
  public ResponseEntity<NotificationResponse> markNotificationAsRead(
      @PathVariable UUID id, Authentication authentication) {
    var user = authenticatedUserResolver.requireUser(authentication);
    var notification = notificationService.markAsRead(id, user);
    var response = mapper.toResponse(notification);
    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Shows notification preferences",
      description = "Shows the authenticated user's notification channel preferences.")
  @ApiResponse(responseCode = "200", description = "Preferences retrieved successfully.")
  @GetMapping("/preferences")
  public ResponseEntity<NotificationPreferenceResponse> getPreferences(
      Authentication authentication) {
    var user = authenticatedUserResolver.requireUser(authentication);
    var preferences = preferenceService.getPreferencesForUser(user);
    var response = mapper.toPreferenceResponse(preferences);
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/preferences")
  @Operation(
      summary = "Update notification preferences",
      description =
          "Updates the authenticated user's notification channel preferences. All fields are optional.")
  @ApiResponse(responseCode = "200", description = "Preferences updated successfully.")
  public ResponseEntity<NotificationPreferenceResponse> updatePreferences(
      @RequestBody @Valid UpdateNotificationPreferenceRequest request,
      Authentication authentication) {
    var user = authenticatedUserResolver.requireUser(authentication);
    var command = mapper.toUpdateCommand(request);
    var preferences = preferenceService.updatePreferences(user, command);
    var response = mapper.toPreferenceResponse(preferences);
    return ResponseEntity.ok(response);
  }
}
