package pl.sgorski.expense_splitter.security.rate_limit;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RateLimitType {
  AUTH("AUTH"),
  API("API");

  private final String keyPrefix;
}
