package com.delivery.justonebite.global.config.security;

import com.delivery.justonebite.global.common.jwt.JwtAuthorizationFilter;
import com.delivery.justonebite.user.application.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final AuthService authService;
    private final CustomAuthenticationEntryPoint  authenticationEntryPoint;

    public WebSecurityConfig(
            JwtAuthorizationFilter jwtAuthorizationFilter,
            @Lazy AuthService authService, CustomAuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
        this.authService = authService;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .rememberMe(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("v1/auth/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("v1/auth/logout").authenticated()
                        .requestMatchers("/swagger-ui/**").permitAll()      // Swagger UI HTML/JS/CSS 파일
                        .requestMatchers("/v3/api-docs/**").permitAll()     // OpenAPI JSON/YAML 정의 파일
                        .requestMatchers("/api-docs/**").permitAll()        // SpringDoc v1/v2 호환 경로
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout
                        .logoutUrl("/v1/auth/logout")
                        .addLogoutHandler((request, response, authentication) -> {
                            authService.logout(authentication);
                        })
                        .logoutSuccessHandler((request, response, authentication) ->
                                response.setStatus(HttpServletResponse.SC_OK))
                )
                .addFilterBefore(jwtAuthorizationFilter, LogoutFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                );

        return http.build();
    }
}
