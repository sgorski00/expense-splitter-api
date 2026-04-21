package pl.sgorski.expense_splitter.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.sgorski.expense_splitter.security.jwt.JwtAuthenticationFilter;
import pl.sgorski.expense_splitter.security.jwt.PasswordChangeRequiredFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final UserDetailsService userDetailsService;
  private final PasswordEncoder passwordEncoder;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final PasswordChangeRequiredFilter passwordChangeRequiredFilter;
  private final AccessDeniedHandler accessDeniedHandler;
  private final AuthenticationEntryPoint authenticationEntryPoint;
  private final AuthenticationSuccessHandler oauth2SuccessHandler;
  private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http.authorizeHttpRequests(
            req ->
                req.requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/actuator/**",
                        "/auth/refresh",
                        "/ws/**")
                    .permitAll()
                    .requestMatchers("/auth/logout")
                    .authenticated()
                    .requestMatchers("/auth/**")
                    .not()
                    .authenticated()
                    .requestMatchers("/oauth2/code/**", "/login/auth2/code/**")
                    .not()
                    .authenticated()
                    .requestMatchers(
                        "/expenses/**",
                        "/friendships/**",
                        "/payments/**",
                        "/profile/**",
                        "/users/**",
                        "/notifications/**")
                    .authenticated()
                    .requestMatchers("/admin/**")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .denyAll())
        .csrf(AbstractHttpConfigurer::disable)
        .cors(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .userDetailsService(userDetailsService)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(passwordChangeRequiredFilter, JwtAuthenticationFilter.class)
        .exceptionHandling(
            ex ->
                ex.accessDeniedHandler(accessDeniedHandler)
                    .authenticationEntryPoint(authenticationEntryPoint))
        .oauth2Login(
            oauth ->
                oauth
                    .userInfoEndpoint(user -> user.userService(oauth2UserService))
                    .successHandler(oauth2SuccessHandler))
        .build();
  }

  @Bean
  public AuthenticationManager authenticationManager() {
    var authProvider = new DaoAuthenticationProvider(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder);
    return new ProviderManager(authProvider);
  }
}
