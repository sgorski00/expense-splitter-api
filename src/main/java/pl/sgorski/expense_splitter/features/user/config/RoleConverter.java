package pl.sgorski.expense_splitter.features.user.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pl.sgorski.expense_splitter.features.user.domain.Role;

@Component
public final class RoleConverter implements Converter<String, Role> {

  @Override
  public Role convert(String source) {
    return Role.fromString(source);
  }
}
