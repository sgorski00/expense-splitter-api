package pl.sgorski.expense_splitter.security.rate_limit.filter.impl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import pl.sgorski.expense_splitter.security.rate_limit.filter.RateLimitFilter;

@Component
@ConditionalOnProperty(value = "es.rate-limit.provider", havingValue = "waf")
public final class NoOpRateLimitFilter extends RateLimitFilter {
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    filterChain.doFilter(request, response);
  }
}
