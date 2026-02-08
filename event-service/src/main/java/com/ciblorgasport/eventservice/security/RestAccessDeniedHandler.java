package com.ciblorgasport.eventservice.security;

import java.io.IOException;
import com.ciblorgasport.eventservice.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

public class RestAccessDeniedHandler implements AccessDeniedHandler {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
			throws IOException, ServletException {
		String message = accessDeniedException != null && accessDeniedException.getMessage() != null
			? accessDeniedException.getMessage()
			: "Access is denied";
		ErrorResponse body = new ErrorResponse(HttpServletResponse.SC_FORBIDDEN, "Forbidden", message, request.getRequestURI());
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		MAPPER.writeValue(response.getWriter(), body);
	}
}
