package com.ciblorgasport.eventservice.security;

import java.io.IOException;
import com.ciblorgasport.eventservice.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {

		String jwtInvalid = String.valueOf(request.getAttribute("X-JWT-INVALID"));
		String jwtMissing = String.valueOf(request.getAttribute("X-JWT-MISSING"));
		String jwtError = (String) request.getAttribute("X-JWT-ERROR");

		String message;
		if ("true".equalsIgnoreCase(jwtInvalid)) {
			message = "Invalid or expired Authorization token.";
		} else if ("true".equalsIgnoreCase(jwtMissing)) {
			message = "Authorization token missing or malformed.";
		} else if (jwtError != null) {
			message = "Authentication error: " + jwtError;
		} else if (authException != null && authException.getMessage() != null) {
			message = authException.getMessage();
		} else {
			message = "Unauthorized";
		}

		ErrorResponse body = new ErrorResponse(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized", message, request.getRequestURI());
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		MAPPER.writeValue(response.getWriter(), body);
	}
}
