package com.ciblorgasport.analyticsservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AuthTokenFilter.class);

    private final JwtUtils jwtUtils;

    public AuthTokenFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractToken(request);
            if (token != null && jwtUtils.validateToken(token)) {
                String username = jwtUtils.getUsernameFromToken(token);
                String role = jwtUtils.getRoleFromToken(token);

                if (role != null && !role.startsWith("ROLE_")) {
                    role = "ROLE_" + role;
                }

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        username, null, List.of(new SimpleGrantedAuthority(role)));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            log.warn("Impossible d'authentifier l'utilisateur depuis le JWT : {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
