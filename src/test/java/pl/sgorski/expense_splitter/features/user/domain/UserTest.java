package pl.sgorski.expense_splitter.features.user.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import pl.sgorski.expense_splitter.exceptions.user.DuplicateIdentityException;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;
import pl.sgorski.expense_splitter.notification.domain.UserNotificationPreference;

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

  @Test
  void ensurePreferences_shouldCreatePreferences_whenNull() {
    var user = new User();
    user.setNotificationPreference(null);

    user.ensurePreferences();

    assertNotNull(user.getNotificationPreference());
    assertEquals(user, user.getNotificationPreference().getUser());
  }

  @Test
  void ensurePreferences_shouldNotCreatePreferences_whenAlreadyExists() {
    var preference = new UserNotificationPreference();
    preference.setId(UUID.randomUUID());
    var user = new User();
    user.setNotificationPreference(preference);

    user.ensurePreferences();

    assertNotNull(user.getNotificationPreference());
    assertEquals(preference, user.getNotificationPreference());
  }

  @Test
  void getDisplayName_shouldReturnEmail_whenFirstNameIsNull() {
    var email = "test@example.com";
    var user = new User();
    user.setEmail(email);
    user.setLastName("Doe");

    var result = user.getDisplayName();

    assertEquals(email, result);
  }

  @Test
  void getDisplayName_shouldReturnEmail_whenLastNameIsNull() {
    var email = "test@example.com";
    var user = new User();
    user.setEmail(email);
    user.setFirstName("John");

    var result = user.getDisplayName();

    assertEquals(email, result);
  }

  @Test
  void getDisplayName_shouldReturnEmail_whenBothNamesAreNull() {
    var email = "test@example.com";
    var user = new User();
    user.setEmail(email);

    var result = user.getDisplayName();

    assertEquals(email, result);
  }

  @Test
  void getDisplayName_shouldReturnFullName_whenFullNameIsPresent() {
    var email = "test@example.com";
    var user = new User();
    user.setEmail(email);
    user.setFirstName("John");
    user.setLastName("Doe");

    var result = user.getDisplayName();

    assertEquals("John Doe", result);
  }

  @Test
  void getPassword_shouldReturnNull() {
    var user = new User();

    var result = user.getPassword();

    assertNull(result);
  }

  @Test
  void getPassword_shouldReturnPasswordHash() {
    var passwordHash = "hashed_password";
    var user = new User();
    user.setPasswordHash(passwordHash);

    var result = user.getPassword();

    assertEquals(passwordHash, result);
  }

  @Test
  void isAccountNonExpired_shouldReturnTrue_whenAccountIsNotDeleted() {
    var user = new User();
    user.setDeletedAt(null);

    var result = user.isAccountNonExpired();

    assertTrue(result);
  }

  @Test
  void isAccountNonExpired_shouldReturnFalse_whenAccountIsDeleted() {
    var user = new User();
    user.setDeletedAt(Instant.now());

    var result = user.isAccountNonExpired();

    assertFalse(result);
  }

  @Test
  void isAccountNonLocked_shouldReturnTrue_whenAccountIsNotDeleted() {
    var user = new User();
    user.setDeletedAt(null);

    var result = user.isAccountNonLocked();

    assertTrue(result);
  }

  @Test
  void isAccountNonLocked_shouldReturnFalse_whenAccountIsDeleted() {
    var user = new User();
    user.setDeletedAt(Instant.now());

    var result = user.isAccountNonLocked();

    assertFalse(result);
  }

  @Test
  void isCredentialsNonExpired_shouldReturnTrue() {
    var user = new User();

    var result = user.isCredentialsNonExpired();

    assertTrue(result);
  }

  @Test
  void isEnabled_shouldReturnTrue_whenAccountIsNotDeleted() {
    var user = new User();
    user.setDeletedAt(null);

    var result = user.isEnabled();

    assertTrue(result);
  }

  @Test
  void isEnabled_shouldReturnFalse_whenAccountIsDeleted() {
    var user = new User();
    user.setDeletedAt(Instant.now());

    var result = user.isEnabled();

    assertFalse(result);
  }
}
