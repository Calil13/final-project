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
                                "/auth/register/**",
                                "/auth/refreshToken",
                                "/auth/password/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**")
                        .permitAll()

                        .requestMatchers(HttpMethod.POST, "/auth/logout").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_OWNER")
                        .requestMatchers(HttpMethod.GET, "/category/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/products/").hasAuthority("ROLE_OWNER")
                        .requestMatchers(HttpMethod.POST, "/products").hasAuthority("ROLE_OWNER")
                        .requestMatchers(HttpMethod.GET, "/productImage/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/productImage/**").hasAuthority("ROLE_OWNER")
                        .requestMatchers(HttpMethod.DELETE, "/productImage/**").hasAuthority("ROLE_OWNER")
                        .requestMatchers("/payments/pay/").hasAuthority("ROLE_CUSTOMER")
                        .requestMatchers("/orders/**").hasAuthority("ROLE_CUSTOMER")
                        .requestMatchers("/address/**").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_OWNER")
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
