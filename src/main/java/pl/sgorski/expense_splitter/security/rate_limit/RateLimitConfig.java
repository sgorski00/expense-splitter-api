package pl.sgorski.expense_splitter.security.rate_limit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.time.Duration;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RateLimitConfig {

  private static final long AUTH_RATE_LIMIT_ONE_MIN = 5;
  private static final long API_RATE_LIMIT_ONE_MIN = 200;

  public Bucket authBucket() {
    var bandwidth = buildOneMinuteBandwidth(AUTH_RATE_LIMIT_ONE_MIN);
    return Bucket.builder().addLimit(bandwidth).build();
  }

  public Bucket apiBucket() {
    var bandwidth = buildOneMinuteBandwidth(API_RATE_LIMIT_ONE_MIN);
    return Bucket.builder().addLimit(bandwidth).build();
  }

  private Bandwidth buildOneMinuteBandwidth(long capacity) {
    return Bandwidth.builder()
        .capacity(capacity)
        .refillGreedy(capacity, Duration.ofMinutes(1))
        .build();
  }
}
