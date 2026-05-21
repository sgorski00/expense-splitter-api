package pl.sgorski.expense_splitter.security.access_token;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.HandlerExceptionResolver;
import pl.sgorski.expense_splitter.exceptions.authentication.two_fa.TwoFactorRequiredException;
import pl.sgorski.expense_splitter.security.jwt.JwtProvider;
import pl.sgorski.expense_splitter.security.service.impl.TwoFactorRequiredWhitelistService;
import pl.sgorski.expense_splitter.utils.AuthorizationTokenUtils;

@ExtendWith(MockitoExtension.class)
public class TwoFactorRequiredFilterTest {

  @Mock private JwtProvider jwtProvider;
  @Mock private AccessTokenService accessTokenService;
  @Mock private HandlerExceptionResolver resolver;
  @Mock private TwoFactorRequiredWhitelistService whitelistService;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private FilterChain filterChain;

  private TwoFactorRequiredFilter filter;

  private final String validToken = "valid.jwt.token";
  private final String authHeader = "Bearer " + validToken;

  @BeforeEach
  void setUp() {
    filter =
        new TwoFactorRequiredFilter(resolver, jwtProvider, accessTokenService, whitelistService);
  }

  @Test
  void doFilterInternal_shouldPass_whenNoAuthHeader() throws Exception {
    when(request.getHeader(AuthorizationTokenUtils.AUTHORIZATION_HEADER)).thenReturn(null);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verifyNoInteractions(jwtProvider, resolver, whitelistService);
  }

  @Test
  void doFilterInternal_shouldPass_whenTokenIsUuid() throws Exception {
    String uuidToken = java.util.UUID.randomUUID().toString();
    when(request.getHeader(AuthorizationTokenUtils.AUTHORIZATION_HEADER))
        .thenReturn("Bearer " + uuidToken);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verifyNoInteractions(jwtProvider, resolver, whitelistService);
  }

  @Test
  void doFilterInternal_shouldPass_whenTokenIsInvalid() throws Exception {
    when(request.getHeader(AuthorizationTokenUtils.AUTHORIZATION_HEADER)).thenReturn(authHeader);
    when(jwtProvider.isInvalid(validToken)).thenReturn(true);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(jwtProvider).isInvalid(validToken);
    verifyNoMoreInteractions(jwtProvider);
    verifyNoInteractions(resolver, whitelistService);
  }

  @Test
  void doFilterInternal_shouldPass_when2FANotRequiredInToken() throws Exception {
    var twoFactorClaim = false;
    var payload =
        new AccessTokenPayload(UUID.randomUUID(), "user@example.com", false, twoFactorClaim);
    when(request.getHeader(AuthorizationTokenUtils.AUTHORIZATION_HEADER)).thenReturn(authHeader);
    when(jwtProvider.isInvalid(validToken)).thenReturn(false);
    when(accessTokenService.parse(validToken)).thenReturn(payload);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(accessTokenService).parse(validToken);
  }

  @Test
  void doFilterInternal_shouldBlock_when2FARequiredAndNotWhitelisted() throws Exception {
    var twoFactorClaim = true;
    var payload =
        new AccessTokenPayload(UUID.randomUUID(), "user@example.com", false, twoFactorClaim);
    var path = "/api/data";
    when(request.getHeader(AuthorizationTokenUtils.AUTHORIZATION_HEADER)).thenReturn(authHeader);
    when(request.getRequestURI()).thenReturn(path);
    when(jwtProvider.isInvalid(validToken)).thenReturn(false);
    when(accessTokenService.parse(validToken)).thenReturn(payload);
    when(whitelistService.isWhitelisted(path)).thenReturn(false);

    filter.doFilterInternal(request, response, filterChain);

    verify(resolver)
        .resolveException(
            eq(request), eq(response), isNull(), any(TwoFactorRequiredException.class));
    verify(filterChain, never()).doFilter(any(), any());
  }

  @Test
  void doFilterInternal_shouldPass_when2FARequiredButIsWhitelisted() throws Exception {
    var twoFactorClaim = true;
    var payload =
        new AccessTokenPayload(UUID.randomUUID(), "user@example.com", false, twoFactorClaim);
    var path = "/api/auth/2fa/verify";
    when(request.getHeader(AuthorizationTokenUtils.AUTHORIZATION_HEADER)).thenReturn(authHeader);
    when(request.getRequestURI()).thenReturn(path);
    when(jwtProvider.isInvalid(validToken)).thenReturn(false);
    when(accessTokenService.parse(validToken)).thenReturn(payload);
    when(whitelistService.isWhitelisted(path)).thenReturn(true);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verifyNoInteractions(resolver);
  }

  @Test
  void doFilterInternal_shouldResolveException_whenJwtExceptionOccurs() throws Exception {
    when(request.getHeader(AuthorizationTokenUtils.AUTHORIZATION_HEADER)).thenReturn(authHeader);
    when(jwtProvider.isInvalid(validToken)).thenReturn(false);
    when(accessTokenService.parse(validToken)).thenThrow(new JwtException("Invalid JWT"));

    filter.doFilterInternal(request, response, filterChain);

    verify(resolver).resolveException(eq(request), eq(response), isNull(), any(JwtException.class));
    verify(filterChain, never()).doFilter(any(), any());
  }

  @Test
  void doFilterInternal_shouldResolveException_whenTwoFactorClaimIsMissing() throws Exception {
    when(request.getHeader(AuthorizationTokenUtils.AUTHORIZATION_HEADER)).thenReturn(authHeader);
    when(jwtProvider.isInvalid(validToken)).thenReturn(false);
    when(accessTokenService.parse(validToken)).thenThrow(new NullPointerException());

    filter.doFilterInternal(request, response, filterChain);

    verify(resolver)
        .resolveException(
            eq(request), eq(response), isNull(), any(TwoFactorRequiredException.class));
    verify(filterChain, never()).doFilter(any(), any());
  }
}
