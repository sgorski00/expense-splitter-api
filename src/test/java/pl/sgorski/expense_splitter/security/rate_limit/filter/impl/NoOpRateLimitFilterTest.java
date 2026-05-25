package pl.sgorski.expense_splitter.security.rate_limit.filter.impl;

import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NoOpRateLimitFilterTest {

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private FilterChain filterChain;
  @InjectMocks private NoOpRateLimitFilter filter;

  @Test
  void doFilterInternal_shouldContinueWithFilterChain_withoutAnyModificationToResponse()
      throws Exception {
    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  void doFilterInternal_shouldNotSetAnyRateLimitHeaders_whenExecutingFilter() throws Exception {
    filter.doFilterInternal(request, response, filterChain);

    verify(response, never()).setHeader(contains("X-Rate-Limit"), anyString());
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  void doFilterInternal_shouldPassRequestAndResponseUnmodified_toFilterChain() throws Exception {
    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verifyNoInteractions(request, response);
  }
}
