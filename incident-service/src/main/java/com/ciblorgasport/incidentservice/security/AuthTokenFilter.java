package com.ciblorgasport.incidentservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class AuthTokenFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("\n=== AUTH FILTER START ===");
        System.out.println("URL: " + request.getRequestURI());
        System.out.println("Method: " + request.getMethod());
        
        try {
            String jwt = parseJwt(request);
            System.out.println("JWT extracted: " + (jwt != null ? "YES" : "NO"));
            
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                String role = jwtUtils.getRoleFromJwtToken(jwt);
                
                System.out.println("Username from JWT: " + username);
                System.out.println("Role from JWT: " + role);
                
                // Charge l'utilisateur depuis le service d'authentification
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                System.out.println("UserDetails loaded - Username: " + userDetails.getUsername());
                System.out.println("UserDetails Authorities: " + userDetails.getAuthorities());
                
                // Utilisez les autorités du userDetails, pas celle du token
                List<GrantedAuthority> authorities = List.copyOf(userDetails.getAuthorities());
                
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("Authentication SET in SecurityContextHolder");
            } else {
                System.out.println("JWT validation FAILED or no JWT");
            }
        } catch (UsernameNotFoundException e) {
            System.err.println("UsernameNotFoundException: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error in AuthTokenFilter: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=== AUTH FILTER END ===\n");
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