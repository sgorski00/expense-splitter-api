package pl.sgorski.expense_splitter.security.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import pl.sgorski.expense_splitter.exceptions.authentication.TwoFactorRequiredException;
import pl.sgorski.expense_splitter.security.service.impl.TwoFactorRequiredWhitelistService;
import pl.sgorski.expense_splitter.utils.AuthorizationTokenUtils;
import pl.sgorski.expense_splitter.utils.UuidUtils;

@Component
public final class TwoFactorRequiredFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final HandlerExceptionResolver resolver;
  private final TwoFactorRequiredWhitelistService whitelistService;

  public TwoFactorRequiredFilter(
      @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
      JwtService jwtService,
      TwoFactorRequiredWhitelistService whitelistService) {
    this.jwtService = jwtService;
    this.resolver = resolver;
    this.whitelistService = whitelistService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    var header = request.getHeader(AuthorizationTokenUtils.AUTHORIZATION_HEADER);
    var token = AuthorizationTokenUtils.getTokenFromHeader(header);
    if (token == null || UuidUtils.isValidUuid(token) || jwtService.isTokenInvalid(token)) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      var twoFactorRequired = jwtService.getTwoFactorClaim(token);
      if (twoFactorRequired) {
        var requestPath = request.getRequestURI();
        if (!whitelistService.isWhitelisted(requestPath)) {
          resolver.resolveException(request, response, null, new TwoFactorRequiredException());
          return;
        }
      }
    } catch (JwtException e) {
      resolver.resolveException(request, response, null, e);
      return;
    } catch (NullPointerException e) {
      resolver.resolveException(
          request,
          response,
          null,
          new TwoFactorRequiredException("Two factor claim is missing from token"));
      return;
    }

    filterChain.doFilter(request, response);
  }
}
