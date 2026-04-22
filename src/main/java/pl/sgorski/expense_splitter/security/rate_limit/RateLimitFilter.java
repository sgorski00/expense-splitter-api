package pl.sgorski.expense_splitter.security.rate_limit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import pl.sgorski.expense_splitter.exceptions.TooManyRequestsException;

@Component
@Slf4j
public final class RateLimitFilter extends OncePerRequestFilter {

  private static final String AUTH_PREFIX = "/api/auth/";
  private final RateLimitService rateLimitService;
  private final HandlerExceptionResolver resolver;

  public RateLimitFilter(
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
    log.debug("Trying to connect to: {}; from: {}", path, ip);
    log.debug("Bucket for key: {}; has tokens: {}", key, bucket.getAvailableTokens());
    if (!bucket.tryConsume(1)) {
      resolver.resolveException(request, response, null, new TooManyRequestsException());
      return;
    }
    filterChain.doFilter(request, response);
  }
}
