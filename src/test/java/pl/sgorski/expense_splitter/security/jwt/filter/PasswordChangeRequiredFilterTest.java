package pl.sgorski.expense_splitter.security.jwt.filter;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.HandlerExceptionResolver;
import pl.sgorski.expense_splitter.exceptions.authentication.PasswordChangeRequiredException;
import pl.sgorski.expense_splitter.security.jwt.payload.AccessTokenPayload;
import pl.sgorski.expense_splitter.security.jwt.service.AccessTokenService;
import pl.sgorski.expense_splitter.security.jwt.service.JwtProvider;
import pl.sgorski.expense_splitter.security.service.impl.PasswordChangeRequiredWhitelistService;

@ExtendWith(MockitoExtension.class)
public class PasswordChangeRequiredFilterTest {

  @Mock private JwtProvider jwtProvider;

  @Mock private AccessTokenService accessTokenService;

  @Mock private PasswordChangeRequiredWhitelistService whitelistService;

  @Mock private HandlerExceptionResolver handlerExceptionResolver;

  @InjectMocks private PasswordChangeRequiredFilter filter;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain filterChain;

  @Test
  void doFilterInternal_shouldPass_whenTokenIsNull() throws Exception {
    when(request.getHeader(anyString())).thenReturn(null);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verifyNoMoreInteractions(filterChain);
    verifyNoInteractions(jwtProvider, handlerExceptionResolver);
  }

  @Test
  void doFilterInternal_shouldPass_whenTokenIsUuid() throws Exception {
    var header = "Bearer " + UUID.randomUUID();
    when(request.getHeader(anyString())).thenReturn(header);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verifyNoMoreInteractions(filterChain);
    verifyNoInteractions(jwtProvider, handlerExceptionResolver);
  }

  @Test
  void doFilterInternal_shouldPass_whenTokenIsInvalid() throws Exception {
    var token = "invalidToken";
    var header = "Bearer " + token;
    when(request.getHeader(anyString())).thenReturn(header);
    when(jwtProvider.isInvalid(eq(token))).thenReturn(true);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verify(jwtProvider, times(1)).isInvalid(eq(token));
    verifyNoMoreInteractions(filterChain, jwtProvider);
    verifyNoInteractions(handlerExceptionResolver);
  }

  @Test
  void doFilterInternal_shouldPass_whenPasswordChangeNotRequired() throws Exception {
    var token = "validToken";
    var header = "Bearer " + token;
    var passwordChangeRequired = false;
    var payload =
        new AccessTokenPayload(
            UUID.randomUUID(), "user@example.com", passwordChangeRequired, false);
    when(request.getHeader(anyString())).thenReturn(header);
    when(jwtProvider.isInvalid(eq(token))).thenReturn(false);
    when(accessTokenService.parse(eq(token))).thenReturn(payload);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verify(jwtProvider, times(1)).isInvalid(eq(token));
    verifyNoMoreInteractions(filterChain, jwtProvider);
    verifyNoInteractions(handlerExceptionResolver);
  }

  @Test
  void doFilterInternal_shouldPass_whenPasswordChangeRequiredAndPathIsWhitelisted()
      throws Exception {
    var token = "validToken";
    var header = "Bearer " + token;
    var requestPath = "/allowed/path";
    var passwordChangeRequired = true;
    var payload =
        new AccessTokenPayload(
            UUID.randomUUID(), "user@example.com", passwordChangeRequired, false);
    when(request.getHeader(anyString())).thenReturn(header);
    when(request.getRequestURI()).thenReturn(requestPath);
    when(jwtProvider.isInvalid(eq(token))).thenReturn(false);
    when(accessTokenService.parse(eq(token))).thenReturn(payload);
    when(whitelistService.isWhitelisted(eq(requestPath))).thenReturn(true);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verify(jwtProvider, times(1)).isInvalid(eq(token));
    verify(accessTokenService, times(1)).parse(eq(token));
    verifyNoMoreInteractions(filterChain, jwtProvider);
    verifyNoInteractions(handlerExceptionResolver);
  }

  @Test
  void doFilterInternal_shouldBlock_whenPasswordChangeRequiredAndPathIsNotWhitelisted()
      throws Exception {
    var token = "validToken";
    var header = "Bearer " + token;
    var requestPath = "/allowed/path";
    var passwordChangeRequired = true;
    var payload =
        new AccessTokenPayload(
            UUID.randomUUID(), "user@example.com", passwordChangeRequired, false);
    when(request.getHeader(anyString())).thenReturn(header);
    when(request.getRequestURI()).thenReturn(requestPath);
    when(jwtProvider.isInvalid(eq(token))).thenReturn(false);
    when(accessTokenService.parse(eq(token))).thenReturn(payload);
    when(whitelistService.isWhitelisted(eq(requestPath))).thenReturn(false);

    filter.doFilterInternal(request, response, filterChain);

    verify(handlerExceptionResolver, times(1))
        .resolveException(
            eq(request), eq(response), isNull(), any(PasswordChangeRequiredException.class));
    verify(jwtProvider, times(1)).isInvalid(eq(token));
    verify(accessTokenService, times(1)).parse(eq(token));
    verifyNoMoreInteractions(handlerExceptionResolver, jwtProvider);
    verifyNoInteractions(filterChain);
  }

  @Test
  void doFilterInternal_shouldResolveException_whenJwtExceptionThrown() throws Exception {
    var token = "validToken";
    var header = "Bearer " + token;
    when(request.getHeader(anyString())).thenReturn(header);
    when(jwtProvider.isInvalid(eq(token))).thenReturn(false);
    when(accessTokenService.parse(eq(token))).thenThrow(JwtException.class);

    filter.doFilterInternal(request, response, filterChain);

    verify(handlerExceptionResolver, times(1))
        .resolveException(eq(request), eq(response), isNull(), any(JwtException.class));
    verify(jwtProvider, times(1)).isInvalid(eq(token));
    verify(accessTokenService, times(1)).parse(eq(token));
    verifyNoMoreInteractions(handlerExceptionResolver, jwtProvider);
    verifyNoInteractions(filterChain);
  }

  @Test
  void doFilterInternal_shouldResolveException_whenPasswordChangeClaimMissing() throws Exception {
    var token = "validToken";
    var header = "Bearer " + token;
    when(request.getHeader(anyString())).thenReturn(header);
    when(jwtProvider.isInvalid(eq(token))).thenReturn(false);
    when(accessTokenService.parse(eq(token))).thenThrow(NullPointerException.class);

    filter.doFilterInternal(request, response, filterChain);

    verify(handlerExceptionResolver, times(1))
        .resolveException(
            eq(request), eq(response), isNull(), any(PasswordChangeRequiredException.class));
    verify(jwtProvider, times(1)).isInvalid(eq(token));
    verify(accessTokenService, times(1)).parse(eq(token));
    verifyNoMoreInteractions(handlerExceptionResolver, jwtProvider);
    verifyNoInteractions(filterChain);
  }
}
