package pl.sgorski.expense_splitter.security.oauth2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
public class OAuth2ContextCookieServiceTest {

  private OAuth2ContextCookieService cookieService;
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  @BeforeEach
  void setUp() {
    cookieService = new OAuth2ContextCookieService();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request, response));
  }

  @AfterEach
  void tearDown() {
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  void write_shouldSetCookieInResponse() {
    var token = "some-oauth2-ctx-token";

    cookieService.write(token);

    var setCookieHeader = response.getHeader(HttpHeaders.SET_COOKIE);
    assertTrue(
        setCookieHeader != null
            && setCookieHeader.contains(OAuth2ContextCookieService.COOKIE_NAME + "=" + token));
    assertTrue(setCookieHeader.contains("Max-Age=" + (5 * 60))); // 5 minutes in seconds
    assertTrue(setCookieHeader.contains("HttpOnly"));
    assertTrue(setCookieHeader.contains("Secure"));
    assertTrue(setCookieHeader.contains("SameSite=Lax"));
  }

  @Test
  void read_shouldReturnToken_whenCookieExists() {
    var token = "some-oauth2-ctx-token";
    request.setCookies(new Cookie(OAuth2ContextCookieService.COOKIE_NAME, token));

    var result = cookieService.read();

    assertTrue(result.isPresent());
    assertEquals(token, result.get());
  }

  @Test
  void read_shouldReturnEmpty_whenNoCookiesExist() {
    request.setCookies((Cookie[]) null);

    var result = cookieService.read();

    assertTrue(result.isEmpty());
  }

  @Test
  void read_shouldReturnEmpty_whenCookieNameDoesNotMatch() {
    request.setCookies(new Cookie("other_cookie", "value"));

    var result = cookieService.read();

    assertTrue(result.isEmpty());
  }

  @Test
  void clear_shouldSetCookieWithZeroExpirationInResponse() {
    cookieService.clear();

    var setCookieHeader = response.getHeader(HttpHeaders.SET_COOKIE);
    assertTrue(
        setCookieHeader != null
            && setCookieHeader.contains(OAuth2ContextCookieService.COOKIE_NAME + "="));
    assertTrue(
        setCookieHeader.contains("Max-Age=0")
            || setCookieHeader.contains("Expires=")); // Cleared cookie
  }
}
