package pl.sgorski.expense_splitter.web;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import pl.sgorski.expense_splitter.exceptions.*;

/**
 * Global exception handler for REST API.
 * <br>
 * Handles custom application exceptions, Spring Security exceptions,
 * and common Spring/Java exceptions.
 * <br>
 * All exceptions are transformed to RFC 7807 Problem Details format.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles NotFoundException and its subclasses.
     * <br>
     * Thrown when a requested resource does not exist.
     */
    @ExceptionHandler({NotFoundException.class, NoResourceFoundException.class})
    @ApiResponse(
            responseCode = "404",
            description = "The requested resource was not found.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ProblemDetail.class,
                            description = "RFC 7807 Problem Details response with 404 status and resource not found message."
                    )
            )
    )
    public ProblemDetail handleNotFoundException(Exception ex) {
        var status = HttpStatus.NOT_FOUND;
        var problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle("Not Found");
        problemDetail.setDetail(ex.getMessage());
        log.warn("Resource not found: {}", ex.getMessage());
        return problemDetail;
    }

    /**
     * Handles UserAlreadyExistsException.
     * <br>
     * Thrown when attempting to create a user with an email that already exists.
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ApiResponse(
            responseCode = "409",
            description = "A user with the provided email already exists.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ProblemDetail.class,
                            description = "RFC 7807 Problem Details response with 409 Conflict status and user already exists message."
                    )
            )
    )
    public ProblemDetail handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        var status = HttpStatus.CONFLICT;
        var problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setTitle("User Already Exists");
        log.warn("User already exists: {}", ex.getMessage());
        return problemDetail;
    }

    /**
     * Handles PasswordChangeRequiredException.
     * <br>
     * Thrown when a user must change their password before accessing resources.
     * <br>
     * The user can only access endpoints related to password change.
     */
    @ExceptionHandler(PasswordChangeRequiredException.class)
    @ApiResponse(
            responseCode = "403",
            description = "Password change is required before accessing other resources.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ProblemDetail.class,
                            description = "RFC 7807 Problem Details response indicating that password must be changed."
                    )
            )
    )
    public ProblemDetail handlePasswordChangeRequiredException(PasswordChangeRequiredException ex) {
        var status = HttpStatus.FORBIDDEN;
        var problemDetail = ProblemDetail.forStatusAndDetail(status,
                "Password change is required. Please update your password before accessing other resources.");
        problemDetail.setTitle("Password Change Required");
        log.info("Password change required for user: {}", ex.getMessage());
        return problemDetail;
    }

    /**
     * Handles AccountLinkRequiredException.
     * <br>
     * Thrown when an OAuth2 user needs to link their account to proceed.
     */
    @ExceptionHandler(AccountLinkRequiredException.class)
    @ApiResponse(
            responseCode = "403",
            description = "Account linking is required for this operation.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ProblemDetail.class,
                            description = "RFC 7807 Problem Details response indicating account must be linked."
                    )
            )
    )
    public ProblemDetail handleAccountLinkRequiredException(AccountLinkRequiredException ex) {
        var status = HttpStatus.FORBIDDEN;
        var problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setTitle("Account Linking Required");
        log.info("Account linking required: {}", ex.getMessage());
        return problemDetail;
    }

    /**
     * Handles DuplicateIdentityException.
     * <br>
     * Thrown when attempting to add a duplicate identity to a user.
     */
    @ExceptionHandler(DuplicateIdentityException.class)
    @ApiResponse(
            responseCode = "409",
            description = "User already has an identity linked with this provider.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ProblemDetail.class,
                            description = "RFC 7807 Problem Details response with 409 Conflict status."
                    )
            )
    )
    public ProblemDetail handleDuplicateIdentityException(DuplicateIdentityException ex) {
        var status = HttpStatus.CONFLICT;
        var problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setTitle("Duplicate Identity");
        log.warn("Duplicate identity attempt: {}", ex.getMessage());
        return problemDetail;
    }

    /**
     * Handles FriendshipStatusChangeException.
     * <br>
     * Thrown when attempting to update friendship status that is not PENDING.
     */
    @ExceptionHandler(FriendshipStatusChangeException.class)
    @ApiResponse(
            responseCode = "409",
            description = "Friendship is not in pending status.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ProblemDetail.class,
                            description = "RFC 7807 Problem Details response with 409 Conflict status."
                    )
            )
    )
    public ProblemDetail handleFriendshipStatusChangeException(FriendshipStatusChangeException ex) {
        var status = HttpStatus.CONFLICT;
        var problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setTitle("Cannot Update Friendship Status");
        log.warn("Illegal friendship status update request: {}", ex.getMessage());
        return problemDetail;
    }

    /**
     * Handles AccountLinkingException.
     * <br>
     * Thrown when account linking operation fails (already linked or missing user ID).
     */
    @ExceptionHandler(AccountLinkingException.class)
    @ApiResponse(
            responseCode = "400",
            description = "Account linking operation failed due to invalid state.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ProblemDetail.class,
                            description = "RFC 7807 Problem Details response with 400 Bad Request status."
                    )
            )
    )
    public ProblemDetail handleAccountLinkingException(AccountLinkingException ex) {
        var status = HttpStatus.BAD_REQUEST;
        var problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setTitle("Account Linking Failed");
        log.warn("Account linking error: {}", ex.getMessage());
        return problemDetail;
    }

    /**
     * Handles PasswordOperationException.
     * <br>
     * Thrown when password operation fails (already has password, missing password, etc.).
     */
    @ExceptionHandler(PasswordOperationException.class)
    @ApiResponse(
            responseCode = "400",
            description = "Password operation failed due to invalid account state.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ProblemDetail.class,
                            description = "RFC 7807 Problem Details response with 400 Bad Request status."
                    )
            )
    )
    public ProblemDetail handlePasswordOperationException(PasswordOperationException ex) {
        var status = HttpStatus.BAD_REQUEST;
        var problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setTitle("Password Operation Failed");
        log.warn("Password operation error: {}", ex.getMessage());
        return problemDetail;
    }

    /**
     * Handles InvalidPasswordException.
     * <br>
     * Thrown when provided password is incorrect during change operation.
     */
    @ExceptionHandler(InvalidPasswordException.class)
    @ApiResponse(
            responseCode = "401",
            description = "Provided password is incorrect.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ProblemDetail.class,
                            description = "RFC 7807 Problem Details response with 401 Unauthorized status."
                    )
            )
    )
    public ProblemDetail handleInvalidPasswordException(InvalidPasswordException ex) {
        var status = HttpStatus.UNAUTHORIZED;
        var problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setTitle("Invalid Password");
        log.warn("Invalid password provided: {}", ex.getMessage());
        return problemDetail;
    }

    /**
     * Handles RefreshTokenValidationException.
     * <br>
     * Thrown when refresh token is expired or revoked.
     * User must re-authenticate to obtain a new refresh token.
     */
    @ExceptionHandler(RefreshTokenValidationException.class)
    @ApiResponse(
            responseCode = "401",
            description = "Refresh token is invalid, expired, or has been revoked.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ProblemDetail.class,
                            description = "RFC 7807 Problem Details response with 401 Unauthorized status."
                    )
            )
    )
    public ProblemDetail handleRefreshTokenValidationException(RefreshTokenValidationException ex) {
        var status = HttpStatus.UNAUTHORIZED;
        var problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setTitle("Invalid Refresh Token");
        log.warn("Refresh token validation failed: {}", ex.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(AuthenticationException.class)
    @ApiResponse(
            responseCode = "401",
            description = "Authentication failed. Invalid credentials or token.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ProblemDetail.class,
                            description = "RFC 7807 Problem Details response with 401 Unauthorized status."
                    )
            )
    )
    public ProblemDetail handleAuthenticationException(AuthenticationException ex) {
        var status = HttpStatus.UNAUTHORIZED;
        var problemDetail = ProblemDetail.forStatusAndDetail(status,
                "Authentication failed. Please check your credentials and try again.");
        problemDetail.setTitle("Unauthorized");
        log.warn("Authentication failed: {}", ex.getMessage());
        return problemDetail;
    }

    /**
     * Handles AccessDeniedException.
     * <br>
     * Thrown when a user lacks necessary permissions/authorities.
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ApiResponse(
            responseCode = "403",
            description = "Access denied. Insufficient permissions for this resource.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ProblemDetail.class,
                            description = "RFC 7807 Problem Details response with 403 Forbidden status."
                    )
            )
    )
    public ProblemDetail handleAccessDeniedException(AccessDeniedException ex) {
        var status = HttpStatus.FORBIDDEN;
        var problemDetail = ProblemDetail.forStatusAndDetail(status,
                "Access denied. You do not have permission to access this resource.");
        problemDetail.setTitle("Forbidden");
        log.warn("Access denied: {}", ex.getMessage());
        return problemDetail;
    }

    /**
     * Handles MethodArgumentNotValidException.
     * <br>
     * Thrown when request body validation fails (invalid DTOs).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ApiResponse(
            responseCode = "400",
            description = "Request validation failed. Check the error details for field-specific issues.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ProblemDetail.class,
                            description = "RFC 7807 Problem Details response with validation error details."
                    )
            )
    )
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        var status = HttpStatus.BAD_REQUEST;
        var errorMessage = buildValidationErrorMessage(ex);
        var problemDetail = ProblemDetail.forStatusAndDetail(status, errorMessage);
        problemDetail.setTitle("Validation Failed");
        log.warn("Validation error: {}", errorMessage);
        return problemDetail;
    }

    /**
     * Handles MethodArgumentTypeMismatchException.
     * <br>
     * Thrown when request parameters have incorrect types.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ApiResponse(
            responseCode = "400",
            description = "Request parameter has invalid type.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ProblemDetail.class,
                            description = "RFC 7807 Problem Details response indicating type mismatch."
                    )
            )
    )
    public ProblemDetail handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        var status = HttpStatus.BAD_REQUEST;
        var message = String.format(
                "Parameter '%s' should be of type %s",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"
        );
        var problemDetail = ProblemDetail.forStatusAndDetail(status, message);
        problemDetail.setTitle("Bad Request");
        log.warn("Type mismatch: {}", message);
        return problemDetail;
    }

    /**
     * Handles DataIntegrityViolationException.
     * <br>
     * Thrown when database constraint violations occur (unique constraint, foreign key, etc.).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ApiResponse(
            responseCode = "409",
            description = "Database constraint violation. Data integrity check failed.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ProblemDetail.class,
                            description = "RFC 7807 Problem Details response with 409 Conflict status."
                    )
            )
    )
    public ProblemDetail handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        var status = HttpStatus.CONFLICT;
        var message = extractDataIntegrityErrorMessage(ex);
        var problemDetail = ProblemDetail.forStatusAndDetail(status, message);
        problemDetail.setTitle("Data Integrity Violation");
        log.error("Database constraint violation: {}", ex.getMessage());
        return problemDetail;
    }

    /**
     * Handles MissingServletRequestParameterException.
     * <br>
     * Thrown when required request parameters are missing from the request.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ApiResponse(
            responseCode = "400",
            description = "Required request parameter is missing.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ProblemDetail.class,
                            description = "RFC 7807 Problem Details response with 400 Bad Request status."
                    )
            )
    )
    public ProblemDetail handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        var status = HttpStatus.BAD_REQUEST;
        var message = String.format("Required parameter '%s' is missing.", ex.getParameterName());
        var problemDetail = ProblemDetail.forStatusAndDetail(status, message);
        problemDetail.setTitle("Missing Request Parameter");
        log.warn("Missing request parameter: {}", ex.getParameterName());
        return problemDetail;
    }

    /**
     * Handles InvalidFriendshipOperationException.
     * <br>
     * Thrown when an invalid friendship operation is attempted (e.g., self-request).
     */
    @ExceptionHandler(InvalidFriendshipOperationException.class)
    @ApiResponse(
            responseCode = "400",
            description = "Invalid friendship operation.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ProblemDetail.class,
                            description = "RFC 7807 Problem Details response with 400 Bad Request status."
                    )
            )
    )
    public ProblemDetail handleInvalidFriendshipOperationException(InvalidFriendshipOperationException ex) {
        var status = HttpStatus.BAD_REQUEST;
        var problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setTitle("Invalid Friendship Operation");
        log.warn("Invalid friendship operation: {}", ex.getMessage());
        return problemDetail;
    }

    /**
     * Fallback handler for all unhandled exceptions.
     * <br>
     * Provides a generic 500 response for unexpected errors.
     */
    @ExceptionHandler(Exception.class)
    @ApiResponse(
            responseCode = "500",
            description = "An unexpected server error occurred. The error details are provided in the response body in RFC 7807 Problem Details format.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ProblemDetail.class,
                            description = "Standard RFC 7807 Problem Details response containing error information (type, title, status, detail, instance)."
                    )
            )
    )
    public ProblemDetail handleException(Exception ex) {
        var status = HttpStatus.INTERNAL_SERVER_ERROR;
        var problemDetail = ProblemDetail.forStatusAndDetail(status,
                "An unexpected server error occurred. Please try again later.");
        problemDetail.setTitle("Internal Server Error");
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return problemDetail;
    }

    /**
     * Builds a concise validation error message from all field errors.
     *
     * @param ex the MethodArgumentNotValidException
     * @return formatted error message
     */
    private String buildValidationErrorMessage(MethodArgumentNotValidException ex) {
        var fieldErrors = ex.getBindingResult().getFieldErrors();
        if (fieldErrors.isEmpty()) {
            return "Validation failed";
        }
        var errorMessages = fieldErrors.stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .toList();
        return String.join("; ", errorMessages);
    }

    /**
     * Extracts a user-friendly error message from DataIntegrityViolationException.
     * <br>
     * Attempts to detect common constraint violations and provide specific messages.
     *
     * @param ex the DataIntegrityViolationException
     * @return formatted error message
     */
    private String extractDataIntegrityErrorMessage(DataIntegrityViolationException ex) {
        var cause = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";

        if (cause.contains("unique constraint") || cause.contains("duplicate")) {
            return "A record with this data already exists. Ensure all unique fields have unique values.";
        }
        if (cause.contains("foreign key")) {
            return "Cannot perform this operation due to related data dependencies.";
        }
        if (cause.contains("not null")) {
            return "Required field is missing or null.";
        }
        return "Database constraint violation. Please check your data and try again.";
    }
}
