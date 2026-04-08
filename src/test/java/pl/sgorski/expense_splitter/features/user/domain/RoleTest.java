package pl.sgorski.expense_splitter.features.user.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import pl.sgorski.expense_splitter.exceptions.user.RoleNotFoundException;

public class RoleTest {

  @Test
  void fromString_shouldReturnUserRole_whenValueCorrect() {
    var value = "UsEr";

    var result = Role.fromString(value);

    assertEquals(Role.USER, result);
  }

  @Test
  void fromString_shouldReturnAdminRole_whenValueCorrect() {
    var value = "ADMIN";

    var result = Role.fromString(value);

    assertEquals(Role.ADMIN, result);
  }

  @Test
  void fromString_shouldThrow_whenRoleNotFound() {
    var value = "not-existing";

    assertThrows(RoleNotFoundException.class, () -> Role.fromString(value));
  }

  @Test
  void getAuthority_shouldReturnRoleNameWithPrefix() {
    var role = Role.USER;

    var result = role.getAuthority();

    assertEquals("ROLE_USER", result);
  }
}
