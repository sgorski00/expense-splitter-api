package pl.sgorski.expense_splitter.security.rate_limit;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RateLimitType {
  AUTH("AUTH", 5),
  API("API", 200);

  private final String keyPrefix;
  private final int limit;
}
