package com.delivery.justonebite.global.common.security;

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

    public WebSecurityConfig(JwtAuthorizationFilter jwtAuthorizationFilter, @Lazy AuthService authService) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
        this.authService = authService;
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
                        .requestMatchers(
                                "v1/auth/signup",
                                "v1/auth/signin",
                                "v1/auth/reissue"
                        ).permitAll()
                        .requestMatchers("v1/auth/logout").authenticated()
                        .requestMatchers("/swagger-ui/**").permitAll()      // Swagger UI HTML/JS/CSS 파일
                        .requestMatchers("/v3/api-docs/**").permitAll()     // OpenAPI JSON/YAML 정의 파일
                        .requestMatchers("/api-docs/**").permitAll()        // SpringDoc v1/v2 호환 경로
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
//                        .requestMatchers("v1/**").permitAll()
//                        .anyRequest().permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/v1/auth/logout") // 로그아웃을 처리할 URL
                        .addLogoutHandler((request, response, authentication) -> {
                            authService.logout(authentication);
                        })
                        .logoutSuccessHandler((request, response, authentication) ->
                                response.setStatus(HttpServletResponse.SC_OK))
                )
                .addFilterBefore(jwtAuthorizationFilter, LogoutFilter.class)
//                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            log.error("접근 거부: {}", accessDeniedException.getMessage());
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.");
                        })
                        .authenticationEntryPoint((request, response, authException) -> {
                            log.error("인증 실패: {}", authException.getMessage());
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증되지 않은 사용자입니다.");
                        })
                );

        return http.build();
    }
}
