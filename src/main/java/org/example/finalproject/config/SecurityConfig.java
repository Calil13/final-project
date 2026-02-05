package org.example.finalproject.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.finalproject.exception.GlobalExceptionHandler;
import org.example.finalproject.jwt.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/login",
                                "/auth/admin/login",
                                "/auth/register/**",
                                "/auth/refreshToken",
                                "/auth/password/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**")
                        .permitAll()

                        .requestMatchers(HttpMethod.POST, "/auth/logout").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_OWNER", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/category/**").hasAnyAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/category/**").hasAnyAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/category/**").hasAnyAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/category/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/products/").hasAnyAuthority("ROLE_OWNER", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/products").hasAnyAuthority("ROLE_OWNER")
                        .requestMatchers(HttpMethod.GET, "/productImage/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/productImage/**").hasAnyAuthority("ROLE_OWNER", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/productImage/**").hasAnyAuthority("ROLE_OWNER", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/productReviews/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/productReviews/**").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/productReviews/**").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/productReviews/**").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                        .requestMatchers("/payments/pay/").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                        .requestMatchers("/orders/**").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                        .requestMatchers("/address/**").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_OWNER", "ROLE_ADMIN")
                        .requestMatchers("/favorites/**").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                        .requestMatchers("/user/**").authenticated()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, accessDeniedException) ->
                                GlobalExceptionHandler.unauthorizedResponse(response)
                        )
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                GlobalExceptionHandler.accessDeniedResponse(response)
                        )
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
