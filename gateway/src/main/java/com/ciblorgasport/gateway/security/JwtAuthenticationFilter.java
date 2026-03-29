package com.ciblorgasport.gateway.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        System.out.println("🌐 Gateway Filter - Path: " + path);
        
        // Laisser passer les routes publiques sans vérification JWT
        if (path.startsWith("/auth/") || path.startsWith("/actuator") || path.startsWith("/ws/") || path.startsWith("/ws-")) {
            System.out.println("✅ Public path - allowing without JWT");
            return chain.filter(exchange);
        }
        
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        System.out.println("📋 Authorization Header: " + authHeader);
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("🔑 Token extracted: " + token.substring(0, Math.min(20, token.length())) + "...");
            
            if (jwtUtils.validateJwtToken(token)) {
                System.out.println("✅ JWT is valid!");
                Claims claims = jwtUtils.getClaimsFromJwtToken(token);
                String username = claims.getSubject();
                
                // Normalize role claim name and ensure it has ROLE_ prefix
                String role = claims.get("role", String.class);
                System.out.println("👤 Username: " + username);
                System.out.println("🔑 Role from JWT: " + role);
                
                if (role != null && !role.startsWith("ROLE_")) {
                    role = "ROLE_" + role;
                }
                System.out.println("🔑 Final Role: " + role);
                
                // Créer l'authentification Spring Security
                List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role != null ? role : "ROLE_USER"));
                Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
                
                System.out.println("✅ Authentication created, setting in context");
                
                // Ajouter les claims aux headers pour les services downstream
                ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Username", username)
                    .header("X-User-Roles", role != null ? role : "")
                    .build();
                
                // Définir l'authentification dans le contexte de sécurité réactif et continuer
                return chain.filter(exchange.mutate().request(mutatedRequest).build())
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
            } else {
                System.out.println("❌ JWT validation failed");
            }
        } else {
            System.out.println("❌ No valid Authorization header");
        }
        
        // Unauthorized
        System.out.println("🚫 Returning 401 Unauthorized");
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }
}
