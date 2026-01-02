package com.ciblorgasport.billetterie.client;

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
        RestTemplate restTemplate = new RestTemplate();
        String url = authServiceUrl + "/auth/user/username/" + username;
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map userMap = response.getBody();
                String uname = (String) userMap.get("username");
                String password = userMap.get("password") != null ? (String) userMap.get("password") : "";
                String role = userMap.get("role") != null ? (String) userMap.get("role") : "ROLE_USER";
                return User.withUsername(uname)
                        .password(password)
                        .authorities(role)
                        .build();
            }
        } catch (Exception e) {
            throw new UsernameNotFoundException("User Not Found with username: " + username);
        }
        throw new UsernameNotFoundException("User Not Found with username: " + username);
    }
}
