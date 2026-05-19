package pl.sgorski.expense_splitter.security.rate_limit.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import pl.sgorski.expense_splitter.security.rate_limit.model.RateLimitType;

public class RateLimitConfigTest {

  private final RateLimitConfig rateLimitConfig = new RateLimitConfig();

  @Test
  void authBucket_shouldHaveCorrectCapacity() {
    var bucket = rateLimitConfig.authBucket();

    assertEquals(RateLimitType.AUTH.getLimit(), bucket.getAvailableTokens());
  }

  @Test
  void apiBucket_shouldHaveCorrectCapacity() {
    var bucket = rateLimitConfig.apiBucket();

    assertEquals(RateLimitType.API.getLimit(), bucket.getAvailableTokens());
  }
}
