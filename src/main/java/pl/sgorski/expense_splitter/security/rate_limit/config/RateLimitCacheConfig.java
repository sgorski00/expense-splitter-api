package pl.sgorski.expense_splitter.security.rate_limit.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bucket;
import java.time.Duration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "es.rate-limit.provider", havingValue = "local")
public class RateLimitCacheConfig {

  private static final int MAX_CACHE_SIZE = 10_000;
  private static final Duration CACHE_EXPIRATION = Duration.ofMinutes(10);

  @Bean
  public Cache<String, Bucket> rateLimitCache() {
    return Caffeine.newBuilder()
        .expireAfterAccess(CACHE_EXPIRATION)
        .maximumSize(MAX_CACHE_SIZE)
        .build();
  }
}
