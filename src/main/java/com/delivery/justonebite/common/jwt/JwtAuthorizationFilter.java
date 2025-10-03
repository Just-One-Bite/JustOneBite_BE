package com.delivery.justonebite.common.jwt;

import com.delivery.justonebite.common.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        String bearerJwt = request.getHeader("Authorization");

        if (bearerJwt == null || !bearerJwt.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = jwtUtil.substringToken(bearerJwt);

        if (StringUtils.hasText(token)) {

            log.info(token);

            if (!jwtUtil.validateToken(token)) {
                log.error("Token Validation Failed: 유효하지 않거나 만료된 토큰입니다.");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않거나 만료된 토큰입니다.");
                return;
            }

            Claims info = jwtUtil.extractClaims(token);
            log.info("JWT Claims : {}", info);

            try {
                setAuthentication(info.getSubject());
                log.info("Subject : {}", info.getSubject());
            } catch (UsernameNotFoundException e) {
                log.error("Authentication Setup Failed - User Not Found: 사용자 인증 정보를 찾을 수 없습니다.}");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "사용자 인증 정보를 찾을 수 없습니다.");
                return;
            } catch (Exception e) {
                log.error("Error: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
//            if (!jwtUtil.validateToken(token)) {
//                log.error("Token Error");
//                return;
//            }
//
//            Claims info = jwtUtil.extractClaims(token);
//            log.info("JWT Claims : {}", info);
//
//            try {
//                setAuthentication(info.getSubject());
//                log.info("Subject: {}", info.getSubject());
//            } catch (Exception e) {
//                log.error("Error: {}", e.getMessage());
//                return;
//            }
        }

        chain.doFilter(request, response);
    }

    public void setAuthentication(String email) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(email);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    private Authentication createAuthentication(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
