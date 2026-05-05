package pl.sgorski.expense_splitter.security.sentry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import io.sentry.Sentry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.security.authenticated.AuthenticatedUserResolver;

@ExtendWith(MockitoExtension.class)
public class SentryContextFilterTest {

  @Mock private AuthenticatedUserResolver authenticatedUserResolver;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private FilterChain filterChain;
  @Mock private Authentication authentication;
  @Mock private SecurityContext securityContext;

  @InjectMocks private SentryContextFilter sentryContextFilter;

  private MockedStatic<Sentry> sentryMock;
  private MockedStatic<SecurityContextHolder> securityContextHolderMock;

  @BeforeEach
  void setUp() {
    sentryMock = mockStatic(Sentry.class);
    securityContextHolderMock = mockStatic(SecurityContextHolder.class);
    securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
  }

  @AfterEach
  void tearDown() {
    sentryMock.close();
    securityContextHolderMock.close();
  }

  @Test
  void doFilterInternal_shouldSetSentryUser_whenAuthenticated() throws Exception {
    // Given
    UUID userId = UUID.randomUUID();
    User user = new User();
    user.setId(userId);
    user.setEmail("user@example.com");

    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authenticatedUserResolver.requireUser(authentication)).thenReturn(user);

    // When
    sentryContextFilter.doFilterInternal(request, response, filterChain);

    // Then
    ArgumentCaptor<io.sentry.protocol.User> sentryUserCaptor =
        ArgumentCaptor.forClass(io.sentry.protocol.User.class);
    sentryMock.verify(() -> Sentry.setUser(sentryUserCaptor.capture()));

    io.sentry.protocol.User capturedUser = sentryUserCaptor.getValue();
    assertEquals(userId.toString(), capturedUser.getId());
    assertEquals(user.getEmail(), capturedUser.getEmail());
    assertEquals(user.getUsername(), capturedUser.getUsername());

    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_shouldNotSetSentryUser_whenNotAuthenticated() throws Exception {
    // Given
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(false);

    // When
    sentryContextFilter.doFilterInternal(request, response, filterChain);

    // Then
    sentryMock.verify(() -> Sentry.setUser(any()), never());
    verifyNoInteractions(authenticatedUserResolver);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_shouldNotSetSentryUser_whenAuthenticationIsNull() throws Exception {
    // Given
    when(securityContext.getAuthentication()).thenReturn(null);

    // When
    sentryContextFilter.doFilterInternal(request, response, filterChain);

    // Then
    sentryMock.verify(() -> Sentry.setUser(any()), never());
    verifyNoInteractions(authenticatedUserResolver);
    verify(filterChain).doFilter(request, response);
  }
}
