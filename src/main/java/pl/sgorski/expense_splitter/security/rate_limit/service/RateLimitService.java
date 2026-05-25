package pl.sgorski.expense_splitter.security.rate_limit.service;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.security.rate_limit.config.RateLimitConfig;
import pl.sgorski.expense_splitter.security.rate_limit.model.RateLimitType;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "es.rate-limit.provider", havingValue = "local")
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
