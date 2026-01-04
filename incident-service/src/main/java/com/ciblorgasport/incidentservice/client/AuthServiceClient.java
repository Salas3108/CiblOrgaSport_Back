package com.ciblorgasport.incidentservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Map;

@Component
public class AuthServiceClient {
    @Value("${auth-service.url:http://localhost:8081}")
    private String authServiceUrl;

    public UserDetails fetchUserByUsername(String username) {
        System.out.println("=== AUTH SERVICE CLIENT (REAL) ===");
        System.out.println("Fetching user: " + username);
        
        // NETTOYEZ l'URL pour enlever les espaces
        String cleanAuthServiceUrl = authServiceUrl.trim();
        System.out.println("Auth service URL (cleaned): '" + cleanAuthServiceUrl + "'");
        
        // Construisez l'URL proprement
        String url = cleanAuthServiceUrl + "/auth/user/username/" + username;
        System.out.println("Full URL: " + url);
        
        RestTemplate restTemplate = new RestTemplate();
        
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            System.out.println("Response status: " + response.getStatusCode());
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map userMap = response.getBody();
                System.out.println("Response body: " + userMap);
                
                String uname = (String) userMap.get("username");
                String password = userMap.get("password") != null ? (String) userMap.get("password") : "";
                String role = userMap.get("role") != null ? (String) userMap.get("role") : "ROLE_USER";
                
                System.out.println("Username from response: " + uname);
                System.out.println("Role from response: " + role);
                
                // Assurez-vous que le rôle a ROLE_
                if (role != null && !role.startsWith("ROLE_")) {
                    role = "ROLE_" + role;
                }
                
                System.out.println("Final role: " + role);
                
                return User.withUsername(uname)
                        .password(password)
                        .authorities(role)
                        .build();
            } else {
                System.out.println("Error: No response body or non-2xx status");
            }
        } catch (Exception e) {
            System.err.println("ERROR calling auth service: " + e.getMessage());
            e.printStackTrace();
            
            // FALLBACK: Retournez un utilisateur mock si le service est down
            System.out.println("Using FALLBACK mock user");
            return createFallbackUser(username);
        }
        throw new UsernameNotFoundException("User Not Found with username: " + username);
    }
    
    private UserDetails createFallbackUser(String username) {
        // Logique de fallback
        if ("admin2".equals(username)) {
            return User.withUsername("admin2")
                    .password("")
                    .authorities("ROLE_ADMIN")
                    .build();
        }
        // Ajoutez d'autres utilisateurs si besoin
        
        return User.withUsername(username)
                .password("")
                .authorities("ROLE_USER")
                .build();
    }
}