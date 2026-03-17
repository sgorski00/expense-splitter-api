package pl.sgorski.expense_splitter.features.auth.controller;

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
@RequestMapping(value = "/auth", version = "1")
public final class AuthController {

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        var response = new LoginResponse("jwt-token", "refresh-token"); // TODO: implement
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest request) {
        var path = URI.create("/path/to/user"); // TODO: implement
        return ResponseEntity.created(path).build();
    }
}
