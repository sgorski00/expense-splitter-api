package pl.sgorski.expense_splitter.features.user.dto.contract;

import org.jspecify.annotations.Nullable;

public interface PasswordChange {
  @Nullable String newPassword();

  @Nullable String repeatNewPassword();
}
