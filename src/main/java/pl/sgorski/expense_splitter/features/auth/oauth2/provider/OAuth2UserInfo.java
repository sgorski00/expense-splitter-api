package pl.sgorski.expense_splitter.features.auth.oauth2.provider;

import java.util.Map;
import lombok.Data;
import org.jspecify.annotations.Nullable;
import pl.sgorski.expense_splitter.features.auth.oauth2.AuthProvider;

@Data
public abstract class OAuth2UserInfo {

  protected Map<String, Object> attributes;
  protected AuthProvider provider;
  protected String providerId;
  protected String email;
  @Nullable protected String firstName;
  @Nullable protected String lastName;
}
