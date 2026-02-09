package com.ciblorgasport.incidentservice.controller;

import com.ciblorgasport.incidentservice.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/auth")
    public ResponseEntity<Map<String,Object>> auth(@RequestHeader(value = "Authorization", required = false) String rawAuth) {
        Map<String,Object> out = new HashMap<>();
        boolean headerPresent = StringUtils.hasText(rawAuth);
        out.put("headerPresent", headerPresent);
        out.put("rawAuth", headerPresent ? rawAuth : null);
        String token = parseJwt(rawAuth);
        out.put("tokenPresent", token != null);
        if (token != null) {
            boolean valid = jwtUtils.validateJwtToken(token);
            out.put("valid", valid);
            if (valid) {
                out.put("username", jwtUtils.getUserNameFromJwtToken(token));
                out.put("roles", jwtUtils.getAuthoritiesFromToken(token).stream().map(a -> a.getAuthority()).collect(Collectors.toList()));
            } else {
                out.put("message", "token invalid or signature mismatch");
            }
        }
        return ResponseEntity.ok(out);
    }

    @GetMapping("/attributes")
    public ResponseEntity<Map<String,Object>> attributes(HttpServletRequest request) {
        Map<String,Object> out = new HashMap<>();
        out.put("headerPresent", request.getHeader("Authorization") != null);
        out.put("jwtInvalid", request.getAttribute("X-JWT-INVALID"));
        out.put("jwtMissing", request.getAttribute("X-JWT-MISSING"));
        out.put("jwtError", request.getAttribute("X-JWT-ERROR"));
        out.put("jwtUsername", request.getAttribute("X-JWT-USERNAME"));
        out.put("jwtAuthorities", request.getAttribute("X-JWT-AUTHORITIES"));
        return ResponseEntity.ok(out);
    }

    private String parseJwt(String headerAuth) {
        if (!StringUtils.hasText(headerAuth)) return null;
        String t = headerAuth.trim();
        if (t.length() >= 7 && t.regionMatches(true, 0, "Bearer ", 0, 7)) {
            String token = t.substring(7).trim();
            return StringUtils.hasText(token) ? token : null;
        }
        long dotCount = t.chars().filter(ch -> ch == '.').count();
        return dotCount == 2 ? t : null;
    }
}
