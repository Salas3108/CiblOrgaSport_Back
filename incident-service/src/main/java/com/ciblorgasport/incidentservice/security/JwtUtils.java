package com.ciblorgasport.incidentservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    @Value("${ciblorgasport.app.jwtSecret}")
    private String jwtSecret;
    @Value("${ciblorgasport.app.jwtExpirationMs}")
    private int jwtExpirationMs;
    public Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    public String generateJwtToken(Authentication authentication) {
        org.springframework.security.core.userdetails.User userPrincipal = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("role", userPrincipal.getAuthorities().stream().findFirst().get().getAuthority())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    public String getRoleFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }
    public Long getUserIdFromJwtToken(String token) {
        Object val = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId");
        if (val == null) return null;
        if (val instanceof Number) return ((Number) val).longValue();
        try { return Long.parseLong(val.toString()); } catch (NumberFormatException e) { return null; }
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException e) {
            logger.error("JWT validation error: {}", e.getMessage());
        }
        return false;
    }

    // Add a method that returns parsed authorities from the JWT (robustly handles multiple claim formats).
    public List<GrantedAuthority> getAuthoritiesFromToken(String token) {
        if (!StringUtils.hasText(token)) return Collections.emptyList();
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return Collections.emptyList();
            String payload = parts[1];

            byte[] decoded;
            try {
                decoded = Base64.getUrlDecoder().decode(payload);
            } catch (IllegalArgumentException e) {
                // fallback to standard decoder
                decoded = Base64.getDecoder().decode(payload);
            }
            String json = new String(decoded, StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            List<String> items = new ArrayList<>();
            if (root.has("authorities")) {
                items.addAll(extractAuthorityStrings(root.get("authorities"), mapper));
            } else if (root.has("roles")) {
                items.addAll(extractAuthorityStrings(root.get("roles"), mapper));
            } else if (root.has("scope")) {
                items.addAll(extractAuthorityStrings(root.get("scope"), mapper));
            } else if (root.has("scp")) {
                items.addAll(extractAuthorityStrings(root.get("scp"), mapper));
            }

            return items.stream()
                    .filter(StringUtils::hasText)
                    .map(String::trim)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Don't leak internal details; if parsing fails return an empty list
            return Collections.emptyList();
        }
    }

    // Helper: normalize different claim shapes into a list of strings
    private List<String> extractAuthorityStrings(JsonNode node, ObjectMapper mapper) {
        List<String> list = new ArrayList<>();
        if (node == null) return list;
        try {
            if (node.isArray()) {
                for (JsonNode el : node) {
                    if (el.isTextual()) list.add(el.asText());
                    else if (el.isObject()) {
                        if (el.has("authority")) list.add(el.get("authority").asText());
                        else if (el.has("role")) list.add(el.get("role").asText());
                        else list.add(el.toString());
                    } else {
                        list.add(el.asText());
                    }
                }
            } else if (node.isTextual()) {
                String s = node.asText().trim();
                // possibly a JSON array encoded as a string
                if (s.startsWith("[") && s.endsWith("]")) {
                    JsonNode arr = mapper.readTree(s);
                    if (arr.isArray()) {
                        for (JsonNode el : arr) {
                            if (el.isTextual()) list.add(el.asText());
                            else if (el.isObject() && el.has("authority")) list.add(el.get("authority").asText());
                            else list.add(el.toString());
                        }
                    }
                } else if (s.contains(",")) {
                    for (String part : s.split(",")) list.add(part.trim());
                } else if (s.contains(" ")) {
                    for (String part : s.split("\\s+")) list.add(part.trim());
                } else {
                    list.add(s);
                }
            } else {
                list.add(node.asText());
            }
        } catch (Exception ignore) {
            // ignore parse errors for robustness
        }
        return list;
    }
}
