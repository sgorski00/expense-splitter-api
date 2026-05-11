package pl.sgorski.expense_splitter.IT.base.factory;

import java.util.Objects;
import org.jspecify.annotations.Nullable;
import pl.sgorski.expense_splitter.features.auth.two_fa.domain.UserTwoFactor;
import pl.sgorski.expense_splitter.features.user.domain.Role;
import pl.sgorski.expense_splitter.features.user.domain.User;

public final class UserTestFactory {

  private UserTestFactory() {}

  public static User createUser(String email, String passwordHash) {
    return createUser(email, passwordHash, false, null, false);
  }

  public static User createUser(
      String email, String passwordHash, boolean twoFaEnabled, @Nullable String twoFaSecret) {
    return createUser(email, passwordHash, twoFaEnabled, twoFaSecret, false);
  }

  public static User createUserWithPasswordChange(
      String email, String passwordHash, boolean passwordForChange) {
    return createUser(email, passwordHash, false, null, passwordForChange);
  }

  public static User createUserWithTwoFa(String email, String passwordHash, String twoFaSecret) {
    return createUser(email, passwordHash, true, twoFaSecret, false);
  }

  private static User createUser(
      String email,
      String passwordHash,
      boolean twoFaEnabled,
      @Nullable String twoFaSecret,
      boolean passwordForChange) {
    var user = new User();
    user.setEmail(email);
    user.setPasswordHash(passwordHash);
    user.setPasswordForChange(passwordForChange);
    user.setRole(Role.USER);
    user.setFirstName("John");
    user.setLastName("Doe");
    if (twoFaEnabled) {
      Objects.requireNonNull(
          twoFaSecret,
          "Two-factor secret must be provided when two-factor authentication is enabled");
      var twoFa = new UserTwoFactor();
      twoFa.setSecret(twoFaSecret);
      twoFa.setUser(user);
      twoFa.setEnabled(true);
      user.setTwoFactor(twoFa);
    }
    return user;
  }
}
