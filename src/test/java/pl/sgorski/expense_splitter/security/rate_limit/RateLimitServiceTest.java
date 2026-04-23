package pl.sgorski.expense_splitter.security.rate_limit;

import static org.junit.jupiter.api.Assertions.*;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RateLimitServiceTest {

  private RateLimitService rateLimitService;

  @BeforeEach
  void setUp() {
    Cache<String, Bucket> rateLimitCache = Caffeine.newBuilder().build();
    var rateLimitConfig = new RateLimitConfig();
    rateLimitService = new RateLimitService(rateLimitConfig, rateLimitCache);
  }

  @Test
  void resolveBucket_shouldReturnCachedAuthBucket_whenSameKeys() {
    var key = "AUTH:127.0.0.1";
    var type = RateLimitType.AUTH;

    var bucket1 = rateLimitService.resolveBucket(key, type);
    var bucket2 = rateLimitService.resolveBucket(key, type);

    assertSame(bucket1, bucket2);
  }

  @Test
  void resolveBucket_shouldCreateDifferentAuthBucket_whenDifferentKeys() {
    var key1 = "AUTH:127.0.0.1";
    var key2 = "AUTH:127.0.0.2";
    var type = RateLimitType.AUTH;

    var bucket1 = rateLimitService.resolveBucket(key1, type);
    var bucket2 = rateLimitService.resolveBucket(key2, type);

    assertNotSame(bucket1, bucket2);
  }

  @Test
  void resolveBucket_shouldReturnCachedApiBucket_whenSameKeys() {
    var key = "API:127.0.0.1";
    var type = RateLimitType.API;

    var bucket1 = rateLimitService.resolveBucket(key, type);
    var bucket2 = rateLimitService.resolveBucket(key, type);

    assertSame(bucket1, bucket2);
  }

  @Test
  void resolveBucket_shouldCreateDifferentApiBucket_whenDifferentKeys() {
    var key1 = "API:127.0.0.1";
    var key2 = "API:127.0.0.2";
    var type = RateLimitType.API;

    var bucket1 = rateLimitService.resolveBucket(key1, type);
    var bucket2 = rateLimitService.resolveBucket(key2, type);

    assertNotSame(bucket1, bucket2);
  }

  @Test
  void resolveBucket_shouldHaveDifferentLimits() {
    var apiKey = "API:127.0.0.1";
    var authKey = "AUTH:127.0.0.1";
    var authBucket = rateLimitService.resolveBucket(authKey, RateLimitType.AUTH);
    var apiBucket = rateLimitService.resolveBucket(apiKey, RateLimitType.API);

    authBucket.tryConsumeAsMuchAsPossible();
    assertFalse(authBucket.tryConsume(1));
    assertTrue(apiBucket.tryConsume(1));
  }
}
