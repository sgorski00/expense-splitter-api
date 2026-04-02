package pl.sgorski.expense_splitter.security.jwt;

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
import pl.sgorski.expense_splitter.exceptions.PasswordChangeRequiredException;
import pl.sgorski.expense_splitter.security.service.WhitelistService;

@ExtendWith(MockitoExtension.class)
public class PasswordChangeRequiredFilterTest {

  @Mock private JwtService jwtService;

  @Mock private WhitelistService whitelistService;

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
    verifyNoInteractions(jwtService, handlerExceptionResolver);
  }

  @Test
  void doFilterInternal_shouldPass_whenTokenIsUuid() throws Exception {
    var header = "Bearer " + UUID.randomUUID();
    when(request.getHeader(anyString())).thenReturn(header);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verifyNoMoreInteractions(filterChain);
    verifyNoInteractions(jwtService, handlerExceptionResolver);
  }

  @Test
  void doFilterInternal_shouldPass_whenTokenIsInvalid() throws Exception {
    var token = "invalidToken";
    var header = "Bearer " + token;
    when(request.getHeader(anyString())).thenReturn(header);
    when(jwtService.isTokenInvalid(eq(token))).thenReturn(true);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verify(jwtService, times(1)).isTokenInvalid(eq(token));
    verifyNoMoreInteractions(filterChain, jwtService);
    verifyNoInteractions(handlerExceptionResolver);
  }

  @Test
  void doFilterInternal_shouldPass_whenPasswordChangeNotRequired() throws Exception {
    var token = "validToken";
    var header = "Bearer " + token;
    when(request.getHeader(anyString())).thenReturn(header);
    when(jwtService.isTokenInvalid(eq(token))).thenReturn(false);
    when(jwtService.getPasswordChangeClaim(eq(token))).thenReturn(false);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verify(jwtService, times(1)).isTokenInvalid(eq(token));
    verify(jwtService, times(1)).getPasswordChangeClaim(eq(token));
    verifyNoMoreInteractions(filterChain, jwtService);
    verifyNoInteractions(handlerExceptionResolver);
  }

  @Test
  void doFilterInternal_shouldPass_whenPasswordChangeRequiredAndPathIsWhitelisted()
      throws Exception {
    var token = "validToken";
    var header = "Bearer " + token;
    var requestPath = "/allowed/path";
    when(request.getHeader(anyString())).thenReturn(header);
    when(request.getRequestURI()).thenReturn(requestPath);
    when(jwtService.isTokenInvalid(eq(token))).thenReturn(false);
    when(jwtService.getPasswordChangeClaim(eq(token))).thenReturn(true);
    when(whitelistService.isWhitelisted(eq(requestPath))).thenReturn(true);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verify(jwtService, times(1)).isTokenInvalid(eq(token));
    verify(jwtService, times(1)).getPasswordChangeClaim(eq(token));
    verifyNoMoreInteractions(filterChain, jwtService);
    verifyNoInteractions(handlerExceptionResolver);
  }

  @Test
  void doFilterInternal_shouldBlock_whenPasswordChangeRequiredAndPathIsNotWhitelisted()
      throws Exception {
    var token = "validToken";
    var header = "Bearer " + token;
    var requestPath = "/allowed/path";
    when(request.getHeader(anyString())).thenReturn(header);
    when(request.getRequestURI()).thenReturn(requestPath);
    when(jwtService.isTokenInvalid(eq(token))).thenReturn(false);
    when(jwtService.getPasswordChangeClaim(eq(token))).thenReturn(true);
    when(whitelistService.isWhitelisted(eq(requestPath))).thenReturn(false);

    filter.doFilterInternal(request, response, filterChain);

    verify(handlerExceptionResolver, times(1))
        .resolveException(
            eq(request), eq(response), isNull(), any(PasswordChangeRequiredException.class));
    verify(jwtService, times(1)).isTokenInvalid(eq(token));
    verify(jwtService, times(1)).getPasswordChangeClaim(eq(token));
    verifyNoMoreInteractions(handlerExceptionResolver, jwtService);
    verifyNoInteractions(filterChain);
  }

  @Test
  void doFilterInternal_shouldResolveException_whenJwtExceptionThrown() throws Exception {
    var token = "validToken";
    var header = "Bearer " + token;
    when(request.getHeader(anyString())).thenReturn(header);
    when(jwtService.isTokenInvalid(eq(token))).thenReturn(false);
    when(jwtService.getPasswordChangeClaim(eq(token))).thenThrow(JwtException.class);

    filter.doFilterInternal(request, response, filterChain);

    verify(handlerExceptionResolver, times(1))
        .resolveException(eq(request), eq(response), isNull(), any(JwtException.class));
    verify(jwtService, times(1)).isTokenInvalid(eq(token));
    verify(jwtService, times(1)).getPasswordChangeClaim(eq(token));
    verifyNoMoreInteractions(handlerExceptionResolver, jwtService);
    verifyNoInteractions(filterChain);
  }

  @Test
  void doFilterInternal_shouldResolveException_whenPasswordChangeClaimMissing() throws Exception {
    var token = "validToken";
    var header = "Bearer " + token;
    when(request.getHeader(anyString())).thenReturn(header);
    when(jwtService.isTokenInvalid(eq(token))).thenReturn(false);
    when(jwtService.getPasswordChangeClaim(eq(token))).thenThrow(NullPointerException.class);

    filter.doFilterInternal(request, response, filterChain);

    verify(handlerExceptionResolver, times(1))
        .resolveException(
            eq(request), eq(response), isNull(), any(PasswordChangeRequiredException.class));
    verify(jwtService, times(1)).isTokenInvalid(eq(token));
    verify(jwtService, times(1)).getPasswordChangeClaim(eq(token));
    verifyNoMoreInteractions(handlerExceptionResolver, jwtService);
    verifyNoInteractions(filterChain);
  }
}
