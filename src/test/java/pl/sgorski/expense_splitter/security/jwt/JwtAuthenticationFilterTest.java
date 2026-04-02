package pl.sgorski.expense_splitter.security.jwt;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mockStatic;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

  @Mock private JwtService jwtService;

  @Mock private UserDetailsService userDetailsService;

  @InjectMocks private JwtAuthenticationFilter filter;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain filterChain;

  @Mock private UserDetails userDetails;

  @Mock private Authentication authentication;

  @Test
  void doFilterInternal_shouldPass_whenTokenIsNull() throws Exception {
    when(request.getHeader(anyString())).thenReturn(null);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verifyNoMoreInteractions(filterChain);
    verifyNoInteractions(jwtService, userDetailsService);
  }

  @Test
  void doFilterInternal_shouldPass_whenTokenIsUuid() throws Exception {
    var header = "Bearer " + UUID.randomUUID();
    when(request.getHeader(anyString())).thenReturn(header);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verifyNoMoreInteractions(filterChain);
    verifyNoInteractions(jwtService, userDetailsService);
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
    verifyNoInteractions(userDetailsService);
  }

  @Test
  void doFilterInternal_shouldSetAuthentication_whenTokenIsValidAndContextAuthIsNull()
      throws Exception {
    var token = "validToken";
    var header = "Bearer " + token;
    var email = "user@example.com";
    when(request.getHeader(anyString())).thenReturn(header);
    when(jwtService.isTokenInvalid(eq(token))).thenReturn(false);
    when(jwtService.getEmailFromToken(eq(token))).thenReturn(email);
    when(userDetailsService.loadUserByUsername(eq(email))).thenReturn(userDetails);

    filter.doFilterInternal(request, response, filterChain);

    verify(jwtService, times(1)).isTokenInvalid(eq(token));
    verify(jwtService, times(1)).getEmailFromToken(eq(token));
    verify(userDetailsService, times(1)).loadUserByUsername(eq(email));
    verify(filterChain, times(1)).doFilter(request, response);
    verifyNoMoreInteractions(filterChain, jwtService, userDetailsService);
  }

  @Test
  void doFilterInternal_shouldNotSetAuthentication_whenTokenIsValidButContextAuthIsNotNull()
      throws Exception {
    var token = "validToken";
    var header = "Bearer " + token;
    var email = "user@example.com";
    when(request.getHeader(anyString())).thenReturn(header);
    when(jwtService.isTokenInvalid(eq(token))).thenReturn(false);
    when(jwtService.getEmailFromToken(eq(token))).thenReturn(email);

    var securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);

    try (var staticMock = mockStatic(SecurityContextHolder.class)) {
      staticMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);

      filter.doFilterInternal(request, response, filterChain);

      verify(securityContext, times(1)).getAuthentication();
      verify(securityContext, never()).setAuthentication(any());
      verify(userDetailsService, never()).loadUserByUsername(anyString());
      verify(filterChain, times(1)).doFilter(request, response);
      verifyNoMoreInteractions(filterChain, jwtService, securityContext);
    }
  }
}
