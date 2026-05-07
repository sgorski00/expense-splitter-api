package pl.sgorski.expense_splitter.IT.base.factory;

import pl.sgorski.expense_splitter.features.user.domain.Role;
import pl.sgorski.expense_splitter.features.user.dto.request.CreateUserRequest;

public class UserTestFactory {

  public static CreateUserRequest createUser(String email, String password) {
    return new CreateUserRequest(email, Role.USER, "John", "Doe", password, password);
  }

  public static CreateUserRequest createAdmin(String email, String password) {
    return new CreateUserRequest(email, Role.ADMIN, "John", "Doe", password, password);
  }
}
