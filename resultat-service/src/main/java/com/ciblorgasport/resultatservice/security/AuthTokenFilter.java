package com.ciblorgasport.resultatservice.security;

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
import java.util.Collections;

public class AuthTokenFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("\n=== AUTH FILTER START (Resultat Service) ===");
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
                
                // Pour le moment, nous allons utiliser une approche simple
                // car nous n'avons pas de UserDetailsService dans ce microservice
                // Créer un UserDetails basique avec les infos du JWT
                UserDetails userDetails = createUserDetailsFromJwt(username, role);
                System.out.println("UserDetails created - Username: " + userDetails.getUsername());
                System.out.println("UserDetails Authorities: " + userDetails.getAuthorities());
                
                // Utilisez les autorités du userDetails
                List<GrantedAuthority> authorities = List.copyOf(userDetails.getAuthorities());
                
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                // Ajouter l'ID utilisateur en tant qu'attribut de requête
                // On utilise le nom d'utilisateur comme ID pour le moment
                request.setAttribute("X-User-Id", username);
                
                System.out.println("Authentication SET in SecurityContextHolder");
            } else {
                System.out.println("JWT validation FAILED or no JWT");
            }
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
    
    /**
     * Crée un UserDetails basique à partir des informations du JWT
     * Note: Dans un vrai système, vous devriez appeler le service d'authentification
     * pour récupérer les détails complets de l'utilisateur
     */
    private UserDetails createUserDetailsFromJwt(String username, String role) {
        return new org.springframework.security.core.userdetails.User(
            username,
            "N/A", // Pas de mot de passe pour JWT
            true, // enabled
            true, // accountNonExpired
            true, // credentialsNonExpired
            true, // accountNonLocked
            Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }
}