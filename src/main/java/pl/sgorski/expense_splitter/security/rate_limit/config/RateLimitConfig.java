package pl.sgorski.expense_splitter.security.rate_limit.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.time.Duration;
import lombok.Getter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import pl.sgorski.expense_splitter.security.rate_limit.model.RateLimitType;

@Component
@Getter
@ConditionalOnProperty(name = "es.rate-limit.provider", havingValue = "local")
public final class RateLimitConfig {

  public Bucket authBucket() {
    var authCapacity = RateLimitType.AUTH.getLimit();
    var bandwidth =
        Bandwidth.builder()
            .capacity(authCapacity)
            .refillIntervally(authCapacity, Duration.ofMinutes(1))
            .build();
    return Bucket.builder().addLimit(bandwidth).build();
  }

  public Bucket apiBucket() {
    var apiCapacity = RateLimitType.API.getLimit();
    var bandwidth =
        Bandwidth.builder()
            .capacity(apiCapacity)
            .refillGreedy(apiCapacity, Duration.ofMinutes(1))
            .build();
    return Bucket.builder().addLimit(bandwidth).build();
  }
}
