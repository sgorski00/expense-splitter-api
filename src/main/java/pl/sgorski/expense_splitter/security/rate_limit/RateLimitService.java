package pl.sgorski.expense_splitter.security.rate_limit;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class RateLimitService {

  private final RateLimitConfig rateLimitConfig;
  private final Cache<String, Bucket> rateLimitCache;

  public Bucket resolveBucket(String key, RateLimitType type) {
    return rateLimitCache.get(key, _ -> createBucket(type));
  }

  private Bucket createBucket(RateLimitType type) {
    return switch (type) {
      case AUTH -> rateLimitConfig.authBucket();
      case API -> rateLimitConfig.apiBucket();
    };
  }
}
