package com.ciblorgasport.incidentservice.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.StringUtils;
import org.springframework.http.HttpHeaders;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	@Autowired
	private JwtUtils jwtUtils;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		return "OPTIONS".equalsIgnoreCase(request.getMethod())
				|| path.startsWith("/api/auth/")
				|| path.startsWith("/v3/api-docs")
				|| path.startsWith("/swagger-ui");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String jwt = null;
		try {
			if (SecurityContextHolder.getContext().getAuthentication() == null) {
				String rawAuth = request.getHeader(HttpHeaders.AUTHORIZATION);
				jwt = parseJwt(request);

				logger.debug("Auth header present: {}, jwt extracted: {}", (rawAuth != null && !rawAuth.isBlank()), (jwt != null));

				// Header present but no token parsed (e.g. non-Bearer value) -> mark for debug
				if (rawAuth != null && !rawAuth.isBlank() && jwt == null) {
					logger.debug("Authorization header present but no JWT token parsed");
					request.setAttribute("X-JWT-MISSING", "true");
				}

				if (jwt != null) {
					logger.debug("JWT present (len={}), validating...", jwt.length());
					if (!jwtUtils.validateJwtToken(jwt)) {
						logger.warn("JWT invalid for {} {} (token present but failed validation)", request.getMethod(), request.getRequestURI());
						// mark for debugging in EntryPoint
						request.setAttribute("X-JWT-INVALID", "true");
					} else {
						String username = jwtUtils.getUserNameFromJwtToken(jwt);
						List<GrantedAuthority> authorities = jwtUtils.getAuthoritiesFromToken(jwt);

						logger.debug("JWT ok => username='{}' authorities={}", username, authorities);
						request.setAttribute("X-JWT-USERNAME", username);
						request.setAttribute("X-JWT-AUTHORITIES", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")));

						org.springframework.security.core.userdetails.User userDetails =
								new org.springframework.security.core.userdetails.User(username, "", authorities);

						UsernamePasswordAuthenticationToken authentication =
								new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
						authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
						SecurityContextHolder.getContext().setAuthentication(authentication);
					}
				}
			}
		} catch (Exception e) {
			logger.error("JWT filter error on {} {} (jwtPresent={}): {}", request.getMethod(), request.getRequestURI(), (jwt != null), e.getMessage(), e);
			 // expose a short error marker for diagnostics (avoid leaking sensitive info)
			request.setAttribute("X-JWT-ERROR", e.getClass().getSimpleName());
			// important: do not block here; let Spring Security handle unauthenticated -> 401
		}

		filterChain.doFilter(request, response);
	}

	private String parseJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (!StringUtils.hasText(headerAuth)) {
			return null;
		}
		String trimmed = headerAuth.trim();

		// Accept case-insensitive "Bearer " prefix
		if (trimmed.length() >= 7 && trimmed.regionMatches(true, 0, "Bearer ", 0, 7)) {
			String token = trimmed.substring(7).trim();
			return StringUtils.hasText(token) ? token : null;
		}

		// Accept bare JWT tokens (three-part JWT: header.payload.signature)
		long dotCount = trimmed.chars().filter(ch -> ch == '.').count();
		if (dotCount == 2 && StringUtils.hasText(trimmed)) {
			return trimmed;
		}
		return null;
	}
}
