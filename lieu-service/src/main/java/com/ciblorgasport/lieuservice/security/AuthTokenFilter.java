package com.ciblorgasport.lieuservice.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt == null || jwt.isBlank()) {
                request.setAttribute("X-JWT-MISSING", "true");
            } else if (!jwtUtils.validateJwtToken(jwt)) {
                request.setAttribute("X-JWT-INVALID", "true");
            } else {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                String role = jwtUtils.getRoleFromJwtToken(jwt);
                if (role == null || role.isEmpty()) {
                    role = "ROLE_USER";
                } else if (!role.startsWith("ROLE_")) {
                    role = "ROLE_" + role;
                }
                if (username != null && !username.isEmpty()) {
                    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
                    UserDetails userDetails = User.withUsername(username)
                            .password("")
                            .authorities(authorities)
                            .build();
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            request.setAttribute("X-JWT-ERROR", e.getClass().getSimpleName());
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
