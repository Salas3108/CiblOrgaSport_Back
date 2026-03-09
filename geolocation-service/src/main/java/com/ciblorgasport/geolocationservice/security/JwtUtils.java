package com.ciblorgasport.geolocationservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${ciblorgasport.app.jwtSecret}")
    private String jwtSecret;

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("JWT invalide : {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT expiré : {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT non supporté : {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT vide : {}", e.getMessage());
        }
        return false;
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public String getRoleFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().get("role", String.class);
    }

    public Long getUserIdFromJwtToken(String token) {
        Object userIdObj = Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().get("userId");
        if (userIdObj instanceof Integer) {
            return ((Integer) userIdObj).longValue();
        }
        return (Long) userIdObj;
    }

    private Key key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}
