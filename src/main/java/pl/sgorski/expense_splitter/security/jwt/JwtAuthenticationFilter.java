package pl.sgorski.expense_splitter.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.sgorski.expense_splitter.security.oauth2.AccessTokenCookieResponseHelper;
import pl.sgorski.expense_splitter.utils.UuidUtils;

import java.io.IOException;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public final class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = extractToken(request);

        // if token is an uuid - then skip jwt filter and go next - the refresh token may be passed
        if (token == null || UuidUtils.isValidUuid(token) || !jwtService.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        var email = jwtService.getEmailFromToken(token);
        var securityContext = SecurityContextHolder.getContext();
        if(securityContext.getAuthentication() == null) {
            var userDetails = userDetailsService.loadUserByUsername(email);
            var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            securityContext.setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts JWT token from either Authorization header or httpOnly cookie.
     * Tries Authorization header first, then falls back to accessToken cookie.
     *
     * @param request the HTTP request
     * @return the JWT token if found, null otherwise
     */
    private @Nullable String extractToken(HttpServletRequest request) {
        var header = request.getHeader(AUTHORIZATION_HEADER);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }

        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if (AccessTokenCookieResponseHelper.ACCESS_TOKEN_COOKIE_KEY.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
