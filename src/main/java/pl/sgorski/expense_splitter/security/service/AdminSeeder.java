package pl.sgorski.expense_splitter.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.sgorski.expense_splitter.features.user.domain.Role;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.service.UserService;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements ApplicationRunner {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${es.first-admin.password}")
    private String password;

    @Value("${es.first-admin.email")
    private String email;

    @Override
    public void run(ApplicationArguments args) {
        if(!userService.isAdminPresent()) {
            var admin = new User();
            admin.setEmail(email);
            admin.setPasswordHash(passwordEncoder.encode(password));
            admin.setPasswordForChange(true);
            admin.setRole(Role.ADMIN);
            userService.save(admin);
        }
    }
}
