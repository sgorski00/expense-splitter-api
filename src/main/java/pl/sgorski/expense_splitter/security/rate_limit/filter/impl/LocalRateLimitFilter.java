package pl.sgorski.expense_splitter.security.rate_limit.filter.impl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import pl.sgorski.expense_splitter.exceptions.TooManyRequestsException;
import pl.sgorski.expense_splitter.security.rate_limit.filter.RateLimitFilter;
import pl.sgorski.expense_splitter.security.rate_limit.model.RateLimitType;
import pl.sgorski.expense_splitter.security.rate_limit.service.RateLimitService;

@Component
@Slf4j
@ConditionalOnProperty(name = "es.rate-limit.provider", havingValue = "local")
public final class LocalRateLimitFilter extends RateLimitFilter {

  private static final String RATE_LIMIT_LIMIT_HEADER = "X-Rate-Limit-Limit";
  private static final String RATE_LIMIT_REMAINING_HEADER = "X-Rate-Limit-Remaining";
  private static final String RATE_LIMIT_RETRY_AFTER_HEADER = "X-Rate-Limit-Retry-After-Seconds";
  private static final String AUTH_PREFIX = "/api/auth/";

  private final RateLimitService rateLimitService;
  private final HandlerExceptionResolver resolver;

  public LocalRateLimitFilter(
      RateLimitService rateLimitService,
      @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
    this.rateLimitService = rateLimitService;
    this.resolver = resolver;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    var path = request.getRequestURI();
    var ip = request.getRemoteAddr();
    var type = path.startsWith(AUTH_PREFIX) ? RateLimitType.AUTH : RateLimitType.API;
    var key = String.format("%s:%s", type.getKeyPrefix(), ip);

    var bucket = rateLimitService.resolveBucket(key, type);
    log.debug("Bucket for key: {}; has tokens: {}", key, bucket.getAvailableTokens());

    var probe = bucket.tryConsumeAndReturnRemaining(1);
    response.setHeader(RATE_LIMIT_LIMIT_HEADER, String.valueOf(type.getLimit()));
    response.setHeader(RATE_LIMIT_REMAINING_HEADER, String.valueOf(probe.getRemainingTokens()));
    if (!probe.isConsumed()) {
      var waitForRefillSec = TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill());
      response.setHeader(RATE_LIMIT_RETRY_AFTER_HEADER, String.valueOf(waitForRefillSec));
      resolver.resolveException(request, response, null, new TooManyRequestsException());
      return;
    }
    filterChain.doFilter(request, response);
  }
}
