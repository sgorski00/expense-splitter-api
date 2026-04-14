package pl.sgorski.expense_splitter.features.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.expense_splitter.features.user.dto.response.SimpleUserResponse;
import pl.sgorski.expense_splitter.features.user.mapper.UserMapper;
import pl.sgorski.expense_splitter.features.user.service.UserService;
import pl.sgorski.expense_splitter.validator.text.ValidQuery;

@RestController
@RequestMapping(value = "/users", version = "1.0.0")
@Tag(name = "Users - Public", description = "Endpoints for public (authenticated) users fetching.")
@RequiredArgsConstructor
public final class UserController {

  private final UserService userService;
  private final UserMapper userMapper;

  @GetMapping
  @Operation(
      summary = "List all users matching criteria",
      description = "Retrieves a paginated list of all users that match criteria in the system.")
  @ApiResponses(
      value = {@ApiResponse(responseCode = "200", description = "Users retrieved successfully.")})
  public ResponseEntity<Page<SimpleUserResponse>> getUsers(
      @RequestParam(name = "query") @ValidQuery String query, Pageable pageable) {
    var result = userService.searchUsers(query, pageable).map(userMapper::toSimpleResponse);
    return ResponseEntity.ok(result);
  }
}
