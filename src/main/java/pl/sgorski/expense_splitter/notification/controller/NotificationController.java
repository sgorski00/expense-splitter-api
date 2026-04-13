package pl.sgorski.expense_splitter.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.sgorski.expense_splitter.notification.dto.NotificationResponse;
import pl.sgorski.expense_splitter.notification.service.NotificationService;
import pl.sgorski.expense_splitter.security.authenticated.AuthenticatedUserResolver;

import java.util.UUID;

@RestController
@RequestMapping(value = "/notifications", version = "1.0.0")
@Tag(name = "Notifications", description = "Endpoints for notification management and tracking.")
@RequiredArgsConstructor
public class NotificationController {

    private final AuthenticatedUserResolver authenticatedUserResolver;
    private final NotificationService notificationService;

    @PatchMapping("{id}/read")
    @Operation(
        summary = "Mark notification as read",
        description = "Marks a specific notification as read for the authenticated user.")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Notification marked as read successfully."),
            @ApiResponse(responseCode = "404", description = "Notification not found.")
        })
    public ResponseEntity<NotificationResponse> markNotificationAsRead(@PathVariable UUID id, Authentication authentication) {
        var user = authenticatedUserResolver.requireUser(authentication);
        var notification = notificationService.markAsRead(id, user);
        return ResponseEntity.ok(new NotificationResponse(user.getId(), notification.getTitle(), notification.getBody(), notification.getRead(), notification.getCreatedAt()));
    }
}
