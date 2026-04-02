package pl.sgorski.expense_splitter.features.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.expense_splitter.features.user.domain.Role;
import pl.sgorski.expense_splitter.features.user.dto.response.UserResponse;

@RestController
@RequestMapping(value = "/users", version = "1.0.0")
@Tag(name = "Users - Public", description = "Endpoints for public (authenticated) users fetching.")
public final class UserController {

  @GetMapping("/search")
  @Operation(
      summary = "List all users matching criteria",
      description = "Retrieves a paginated list of all users that match criteria in the system.")
  @ApiResponses(
      value = {@ApiResponse(responseCode = "200", description = "Users retrieved successfully.")})
  public ResponseEntity<Page<UserResponse>> getUsers(
      @RequestParam(name = "query") String query, Pageable pageable) {
    var result =
        new PageImpl<>(
            List.of(
                new UserResponse(
                    UUID.randomUUID(),
                    "user@example.com",
                    Role.USER,
                    Instant.now()))); // TODO: implement
    return ResponseEntity.ok(result);
  }
}
