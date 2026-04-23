package pl.sgorski.expense_splitter.security.rate_limit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

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
