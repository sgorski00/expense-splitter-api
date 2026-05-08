package pl.sgorski.expense_splitter.IT.base.factory;

import java.util.Objects;
import org.jspecify.annotations.Nullable;
import pl.sgorski.expense_splitter.features.auth.two_fa.domain.UserTwoFactor;
import pl.sgorski.expense_splitter.features.user.domain.Role;
import pl.sgorski.expense_splitter.features.user.domain.User;

public class UserTestFactory {

  public static User createUser(
      String email, String passwordHash, boolean twoFaEnabled, @Nullable String twoFaSecret) {
    var user = new User();
    user.setEmail(email);
    user.setPasswordHash(passwordHash);
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
