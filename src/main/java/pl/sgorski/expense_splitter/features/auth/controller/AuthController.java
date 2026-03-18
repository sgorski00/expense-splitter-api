package pl.sgorski.expense_splitter.features.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.sgorski.expense_splitter.features.auth.dto.request.LoginRequest;
import pl.sgorski.expense_splitter.features.auth.dto.request.RegisterRequest;
import pl.sgorski.expense_splitter.features.auth.dto.response.LoginResponse;

import java.net.URI;

@RestController
@RequestMapping(value = "/auth", version = "1.0.0")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration.")
public final class AuthController {

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate user",
            description = "Authenticates a user with email and password, then returns access and refresh tokens."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User authenticated successfully."
            )
    })
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        var response = new LoginResponse("jwt-token", "refresh-token"); // TODO: implement
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register new user",
            description = "Creates a new user account and returns the location of the created user resource."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully."
            )
    })
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest request) {
        var path = URI.create("/path/to/user"); // TODO: implement
        return ResponseEntity.created(path).build();
    }
}
