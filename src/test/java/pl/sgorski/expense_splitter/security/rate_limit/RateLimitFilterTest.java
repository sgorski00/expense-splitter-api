package pl.sgorski.expense_splitter.security.rate_limit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.HandlerExceptionResolver;
import pl.sgorski.expense_splitter.exceptions.TooManyRequestsException;

@ExtendWith(MockitoExtension.class)
public class RateLimitFilterTest {

  @Mock private RateLimitService rateLimitService;
  @Mock private HandlerExceptionResolver resolver;
  @InjectMocks private RateLimitFilter filter;

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private FilterChain filterChain;
  @Mock private Bucket bucket;

  private static final String TEST_IP = "192.168.1.1";
  private static final String AUTH_ENDPOINT = "/api/auth/login";
  private static final String API_ENDPOINT = "/api/expenses";

  @BeforeEach
  void setUp() {
    when(request.getRemoteAddr()).thenReturn(TEST_IP);
  }

  @Test
  void doFilterInternal_shouldSetHeadersAndContinueWithFilterChain_whenRateLimitNotExceeded()
      throws Exception {
    var type = RateLimitType.API;
    var limit = type.getLimit();
    var remaining = limit - 1L;

    when(request.getRequestURI()).thenReturn(API_ENDPOINT);
    when(rateLimitService.resolveBucket(eq("API:" + TEST_IP), eq(type))).thenReturn(bucket);
    var probe = mock(ConsumptionProbe.class);
    when(probe.isConsumed()).thenReturn(true);
    when(probe.getRemainingTokens()).thenReturn(remaining);
    when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(probe);

    filter.doFilterInternal(request, response, filterChain);

    verify(response, times(1)).setHeader("X-Rate-Limit-Limit", String.valueOf(limit));
    verify(response, times(1)).setHeader("X-Rate-Limit-Remaining", String.valueOf(remaining));
    verify(response, never()).setHeader(eq("X-Rate-Limit-Retry-After-Seconds"), anyString());
    verify(filterChain, times(1)).doFilter(request, response);
    verify(resolver, never()).resolveException(any(), any(), any(), any());
  }

  @Test
  void doFilterInternal_shouldSetHeadersAndResolveException_whenRateLimitExceeded()
      throws Exception {
    var type = RateLimitType.AUTH;
    var limit = type.getLimit();
    var remaining = 0L;
    var secondsToWait = 30;
    var nanosToWait = TimeUnit.SECONDS.toNanos(secondsToWait);

    when(request.getRequestURI()).thenReturn(AUTH_ENDPOINT);
    when(rateLimitService.resolveBucket(eq("AUTH:" + TEST_IP), eq(type))).thenReturn(bucket);
    var probe = mock(ConsumptionProbe.class);
    when(probe.isConsumed()).thenReturn(false);
    when(probe.getRemainingTokens()).thenReturn(0L);
    when(probe.getNanosToWaitForRefill()).thenReturn(nanosToWait);
    when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(probe);

    filter.doFilterInternal(request, response, filterChain);

    verify(response, times(1)).setHeader("X-Rate-Limit-Limit", String.valueOf(limit));
    verify(response, times(1)).setHeader("X-Rate-Limit-Remaining", String.valueOf(remaining));
    verify(response, times(1))
        .setHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(secondsToWait));
    verify(resolver, times(1))
        .resolveException(eq(request), eq(response), isNull(), any(TooManyRequestsException.class));
    verify(filterChain, never()).doFilter(request, response);
  }
}
