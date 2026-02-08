package com.ciblorgasport.eventservice.security;

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
            System.out.println("🔍 JWT Token: " + (jwt != null ? "PRESENT" : "ABSENT"));
            
            if (jwt == null || jwt.isBlank()) {
                // mark missing token (useful for error message)
                request.setAttribute("X-JWT-MISSING", "true");
            } else if (!jwtUtils.validateJwtToken(jwt)) {
                // invalid/expired token
                request.setAttribute("X-JWT-INVALID", "true");
            } else {
                System.out.println("✅ JWT Valid!");
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                String role = jwtUtils.getRoleFromJwtToken(jwt);
                
                System.out.println("👤 Username: " + username);
                System.out.println("🔑 Role from JWT: " + role);
                
                // Try a safe default and normalize prefix
                if (role == null || role.isEmpty()) {
                    role = "ROLE_USER";
                } else if (!role.startsWith("ROLE_")) {
                    role = "ROLE_" + role;
                }
                
                System.out.println("🔑 Final Role: " + role);
                
                if (username != null && !username.isEmpty()) {
                    // Créer directement l'authentification à partir du JWT sans appeler auth-service
                    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
                    UserDetails userDetails = User.withUsername(username)
                            .password("") // Le mot de passe n'est pas nécessaire pour JWT
                            .authorities(authorities)
                            .build();
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("✅ Authentication set in SecurityContext!");
                }
            }
        } catch (Exception e) {
            // expose a short error marker for diagnostics (avoid leaking sensitive info)
            request.setAttribute("X-JWT-ERROR", e.getClass().getSimpleName());
            System.out.println("❌ Error in filter: " + e.getMessage());
            e.printStackTrace();
        }
        filterChain.doFilter(request, response);
    }
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        System.out.println("📋 Authorization Header: " + headerAuth);
        
        if (StringUtils.hasText(headerAuth)) {
            System.out.println("📋 Header has text, checking if starts with 'Bearer '");
            if (headerAuth.startsWith("Bearer ")) {
                String token = headerAuth.substring(7);
                System.out.println("✅ Token extracted: " + token.substring(0, Math.min(20, token.length())) + "...");
                return token;
            } else {
                System.out.println("⚠️ Header doesn't start with 'Bearer ', it starts with: " + headerAuth.substring(0, Math.min(10, headerAuth.length())));
            }
        } else {
            System.out.println("❌ No Authorization header found!");
        }
        return null;
    }
}
