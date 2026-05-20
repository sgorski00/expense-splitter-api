package pl.sgorski.expense_splitter.security.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.sgorski.expense_splitter.security.jwt.service.AccessTokenService;
import pl.sgorski.expense_splitter.security.jwt.service.JwtProvider;
import pl.sgorski.expense_splitter.utils.AuthorizationTokenUtils;
import pl.sgorski.expense_splitter.utils.UuidUtils;

@Component
@RequiredArgsConstructor
public final class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtProvider jwtProvider;
  private final AccessTokenService accessTokenService;
  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    var header = request.getHeader(AuthorizationTokenUtils.AUTHORIZATION_HEADER);
    var token = AuthorizationTokenUtils.getTokenFromHeader(header);
    if (token == null || UuidUtils.isValidUuid(token) || jwtProvider.isInvalid(token)) {
      filterChain.doFilter(request, response);
      return;
    }

    var email = accessTokenService.parse(Objects.requireNonNull(token)).email();
    var securityContext = SecurityContextHolder.getContext();
    if (securityContext.getAuthentication() == null) {
      var userDetails = userDetailsService.loadUserByUsername(email);
      var auth =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      securityContext.setAuthentication(auth);
    }
    filterChain.doFilter(request, response);
  }
}
