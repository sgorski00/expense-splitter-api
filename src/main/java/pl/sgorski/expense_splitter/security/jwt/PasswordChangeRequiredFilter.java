package pl.sgorski.expense_splitter.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import pl.sgorski.expense_splitter.exceptions.PasswordChangeRequiredException;

import java.io.IOException;
import java.util.Set;

/**
 * Filter that enforces password change when a user has isPasswordForChange flag set to true.
 * <br>
 * Blocks access to all endpoints except:
 * - /profile/password (PUT and PATCH for password change/set)
 * - /auth/logout (allow users to logout)
 * - /auth/refresh (allow refresh token)
 * - Swagger/OpenAPI documentation endpoints
 */
@Component
public final class PasswordChangeRequiredFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final HandlerExceptionResolver resolver;

    public PasswordChangeRequiredFilter(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver, JwtService jwtService) {
        this.jwtService = jwtService;
        this.resolver = resolver;
    }

    // Endpoints that are allowed when password change is required
    private static final Set<String> WHITELISTED_PATHS = Set.of(
            "/profile/password",
            "/auth/logout",
            "/auth/refresh"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var header = request.getHeader(AUTHORIZATION_HEADER);

        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            var token = header.substring(BEARER_PREFIX.length());
            var isPasswordChangeRequired = jwtService.getPasswordChangeClaim(token);
            if (isPasswordChangeRequired) {
                var requestPath = request.getRequestURI();
                boolean isWhitelisted = WHITELISTED_PATHS.stream()
                        .anyMatch(requestPath::contains);

                if (!isWhitelisted) {
                    resolver.resolveException(request, response, null, new PasswordChangeRequiredException());
                    return;
                }
            }
        } catch (Exception ignored) {}

        filterChain.doFilter(request, response);
    }
}


