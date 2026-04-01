package pl.sgorski.expense_splitter.security.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.sgorski.expense_splitter.security.service.impl.PasswordChangeRequiredWhitelistService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordChangeRequiredWhitelistServiceTest {

    private PasswordChangeRequiredWhitelistService service;

    @BeforeEach
    void setUp() {
        service = new PasswordChangeRequiredWhitelistService();
    }

    @Test
    void isWhitelisted_shouldReturnTrue_whenWhitelistedEndpoints() {
        assertTrue(service.isWhitelisted("/api/auth/logout"));
        assertTrue(service.isWhitelisted("/api/auth/refresh"));
        assertTrue(service.isWhitelisted("/api/profile/password"));
    }


    @Test
    void isWhitelisted_shouldReturnFalse_whenNotWhitelistedEndpoints() {
        assertFalse(service.isWhitelisted("/api/auth/login"));
        assertFalse(service.isWhitelisted("/api/users"));
        assertFalse(service.isWhitelisted("/api/profile"));
    }
}
