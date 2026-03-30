package pl.sgorski.expense_splitter.features.user.domain;

import org.junit.jupiter.api.Test;
import pl.sgorski.expense_splitter.exceptions.DuplicateIdentityException;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void addIdentity_shouldAddIdentityToUser_whenOtherIsNotPresent() {
        var user = new User();
        var identity = new UserIdentity();
        identity.setProvider(AuthProvider.GOOGLE);
        identity.setProviderId("12345");

        user.addIdentity(identity);

        assertTrue(user.getIdentities().contains(identity));
        assertEquals(user, identity.getUser());
    }

    @Test
    void addIdentity_shouldAddIdentityToUser_whenOtherIsFromAnotherProvider() {
        var presentIdentity = new UserIdentity();
        presentIdentity.setProvider(AuthProvider.GOOGLE);
        presentIdentity.setProviderId("12345");
        var presentSet = new HashSet<UserIdentity>();
        presentSet.add(presentIdentity);
        var user = new User();
        user.setIdentities(presentSet);
        var newIdentity = new UserIdentity();
        newIdentity.setProvider(AuthProvider.FACEBOOK);
        newIdentity.setProviderId("12345");

        user.addIdentity(newIdentity);

        assertTrue(user.getIdentities().contains(newIdentity));
        assertTrue(user.getIdentities().contains(presentIdentity));
        assertEquals(user, newIdentity.getUser());
    }

    @Test
    void addIdentity_shouldThrow_whenIdentityFromProviderAlreadyExists() {
        var presentIdentity = new UserIdentity();
        presentIdentity.setProvider(AuthProvider.GOOGLE);
        presentIdentity.setProviderId("12345");
        var presentSet = new HashSet<UserIdentity>();
        presentSet.add(presentIdentity);
        var user = new User();
        user.setIdentities(presentSet);
        var newIdentity = new UserIdentity();
        newIdentity.setProvider(AuthProvider.GOOGLE);
        newIdentity.setProviderId("54321");

        assertThrows(DuplicateIdentityException.class, () -> user.addIdentity(newIdentity));
        assertEquals(Set.of(presentIdentity), user.getIdentities());
    }

    @Test
    void delete_shouldSoftDeleteUser() {
        var user = new User();
        user.setDeletedAt(null);

        user.delete();

        assertNotNull(user.getDeletedAt());
    }
}
