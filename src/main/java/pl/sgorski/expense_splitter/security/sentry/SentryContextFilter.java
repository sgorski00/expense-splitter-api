package pl.sgorski.expense_splitter.security.sentry;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.sgorski.expense_splitter.security.authenticated.AuthenticatedUserResolver;

@Component
@RequiredArgsConstructor
@Slf4j
public final class SentryContextFilter extends OncePerRequestFilter {

  private final AuthenticatedUserResolver authenticatedUserResolver;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    var authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null && authentication.isAuthenticated()) {
      var user = authenticatedUserResolver.requireUser(authentication);
      var sentryUser = new User();
      sentryUser.setId(user.getId().toString());
      sentryUser.setUsername(user.getUsername());
      sentryUser.setEmail(user.getEmail());
      Sentry.setUser(sentryUser);
      log.debug("Sentry context set for user: {}", user.getId());
    }

    filterChain.doFilter(request, response);
  }
}
