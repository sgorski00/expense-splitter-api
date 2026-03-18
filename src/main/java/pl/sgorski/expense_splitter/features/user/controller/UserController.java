package pl.sgorski.expense_splitter.features.user.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sgorski.expense_splitter.features.user.dto.request.CreateUserRequest;
import pl.sgorski.expense_splitter.features.user.dto.response.DetailedUserResponse;
import pl.sgorski.expense_splitter.features.user.dto.response.UserResponse;

import java.awt.print.Pageable;
import java.net.URI;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping(value = "/users", version = "1.0.0")
public final class UserController {

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getUsers(
            Pageable pageable
    ) {
        var result = new PageImpl<>(List.of(new UserResponse(1L, "user@example.com", "USER", Instant.now()))); //TODO: implement
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<DetailedUserResponse> addUser(
            @RequestBody @Valid CreateUserRequest request
    ) {
        var path = URI.create("/path/to/new/user"); //TODO: implement
        return ResponseEntity.created(path).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetailedUserResponse> getUser(
            @PathVariable Long id
    ) {
        var result = new DetailedUserResponse(1L, "user@example.com", "John", "Doe", "USER", Instant.now(), Instant.now(), null); //TODO: implement
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DetailedUserResponse> deactivateUser(
            @PathVariable Long id
    ) {
        //TODO: implement
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DetailedUserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid CreateUserRequest request
    ) {
        var result = new DetailedUserResponse(1L, "user@example.com", "John", "Doe", "USER", Instant.now(), Instant.now(), null); //TODO: implement
        return ResponseEntity.ok(result);
    }
}
