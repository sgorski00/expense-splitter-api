package pl.sgorski.expense_splitter.security.access_token;

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
import pl.sgorski.expense_splitter.exceptions.authentication.PasswordChangeRequiredException;
import pl.sgorski.expense_splitter.security.jwt.JwtProvider;
import pl.sgorski.expense_splitter.security.service.impl.PasswordChangeRequiredWhitelistService;
import pl.sgorski.expense_splitter.utils.AuthorizationTokenUtils;
import pl.sgorski.expense_splitter.utils.UuidUtils;

@Component
public final class PasswordChangeRequiredFilter extends OncePerRequestFilter {

  private final JwtProvider jwtProvider;
  private final AccessTokenService accessTokenService;
  private final HandlerExceptionResolver resolver;
  private final PasswordChangeRequiredWhitelistService whitelistService;

  public PasswordChangeRequiredFilter(
      @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
      JwtProvider jwtProvider,
      AccessTokenService accessTokenService,
      PasswordChangeRequiredWhitelistService whitelistService) {
    this.jwtProvider = jwtProvider;
    this.resolver = resolver;
    this.accessTokenService = accessTokenService;
    this.whitelistService = whitelistService;
  }

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

    try {
      var isPasswordChangeRequired = accessTokenService.parse(token).passwordForChange();
      if (isPasswordChangeRequired) {
        var requestPath = request.getRequestURI();
        if (!whitelistService.isWhitelisted(requestPath)) {
          resolver.resolveException(request, response, null, new PasswordChangeRequiredException());
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
          new PasswordChangeRequiredException("Password change claim is missing from token"));
      return;
    }

    filterChain.doFilter(request, response);
  }
}
