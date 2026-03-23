package pl.sgorski.expense_splitter.features.auth.local.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.exceptions.UserAlreadyExistsException;
import pl.sgorski.expense_splitter.features.auth.dto.command.LoginUserCommand;
import pl.sgorski.expense_splitter.features.auth.dto.command.RegisterUserCommand;
import pl.sgorski.expense_splitter.features.auth.mapper.AuthMapper;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.service.UserService;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LocalAuthService {

    private final UserService userService;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public User registerUser(RegisterUserCommand command) {
        if(userService.isUserPresent(command.email())) {
            throw new UserAlreadyExistsException();
        }

        var user = authMapper.toEntity(command);
        user.setPasswordHash(passwordEncoder.encode(command.newPassword()));
        return userService.save(user);
    }

    /**
     * Validate user's login request and returns user if credentials are correct
     */
    public User login(LoginUserCommand command) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        command.email(),
                        command.password()
                )
        );
        var principal = Objects.requireNonNull(auth.getPrincipal(), "Authentication failed");
        var user = (User) principal;
        return userService.getUser(user.getId());
    }

    /**
     * Method that allows oauth2 users to create local password and login with email and password.
     */
    @Transactional
    public void setLocalPassword(User user, String rawPassword) {
        if(user.getPasswordHash() != null) {
            throw new IllegalStateException("User already has a password. If you want to change it, use change password then.");
        }
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setPasswordForChange(false);
        userService.save(user);
    }

    @Transactional
    public void changePassword(User user, String oldPassword, String newPassword) {
        if(user.getPasswordHash() == null) {
            throw new IllegalStateException("User doesn't have local password yet.");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid current password.");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordForChange(false);
        userService.save(user);
    }
}
